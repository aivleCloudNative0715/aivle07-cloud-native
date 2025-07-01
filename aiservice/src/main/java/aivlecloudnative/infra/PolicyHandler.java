package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationInfoCreationRequested;
import aivlecloudnative.domain.PublicationRequested;
import aivlecloudnative.external.AIServiceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import jakarta.transaction.Transactional; // @Transactional을 위해 명시적으로 임포트

@Configuration // PolicyHandler 클래스를 Spring Configuration 빈으로 등록합니다.
public class PolicyHandler {

        private static final Logger logger = LoggerFactory.getLogger(PolicyHandler.class);

        @Autowired
        private BookWorkRepository bookWorkRepository;

        @Autowired
        private AIServiceSystem aiServiceSystem;

        // PublicationRequested 이벤트 핸들러
        @Bean
        @Transactional // BookWork 생성 및 저장을 트랜잭션으로 묶기 위해
        public Consumer<PublicationRequested> publicationRequestedConsumer() {
                return publicationRequested -> {
                logger.info("\n\n##### [Step 1] PublicationRequested 이벤트 수신 시작 (Consumer): "
                        + publicationRequested.toJson()
                        + "\n");

                // BookWork 객체 생성 (BookWork.createRequestedBookWork 메서드 호출)
                BookWork bookWork = BookWork.createRequestedBookWork(publicationRequested); // <<< 메서드명 변경
                bookWorkRepository.save(bookWork); // BookWork 저장

                // PublicationInfoCreationRequested 이벤트를 발행 (BookWorkController에서 발행하므로 여기서는 발행하지 않음)
                // 여기서는 BookWork 생성까지만 하고, API를 통해 BookWork가 생성되면 이벤트가 발행되도록 하는 것이 일반적입니다.
                logger.info(
                        "##### [Step 2] BookWork 생성 및 초기 상태 설정 완료 (ID: {}). PublicationInfoCreationRequested 이벤트는 BookWorkController에서 발행됩니다.", bookWork.getId());
                };
        }

        /// PublicationInfoCreationRequested 이벤트 핸들러 (AI 처리 시작)
        @Bean
        @Transactional
        public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedConsumer() {
                return publicationInfoCreationRequested -> {
                logger.info("\n\n##### [Step 3] PublicationInfoCreationRequested 이벤트 수신 시작 (Consumer): "
                        + publicationInfoCreationRequested.toJson() + "\n");

                bookWorkRepository.findById(publicationInfoCreationRequested.getId()).ifPresent(bookWork -> {
                        logger.info("##### [Step 3-1] DB에서 BookWork 엔티티 (ID: {}) 조회 성공. 현재 상태: {}",
                                bookWork.getId(),
                                bookWork.getStatus());

                        try {
                        logger.info("##### [Step 4] GPT API 호출을 위한 데이터 준비 중. BookWork ID: {}",
                                bookWork.getId());
                        logger.info("     AI 모델에 전달될 제목: {}", publicationInfoCreationRequested.getTitle());
                        logger.info("     AI 모델에 전달될 요약: {}", publicationInfoCreationRequested.getSummary());
                        logger.info("     AI 모델에 전달될 키워드: {}", publicationInfoCreationRequested.getKeywords());
                        logger.info("     AI 모델에 전달될 저자명: {}", publicationInfoCreationRequested.getAuthorName());
                        logger.info("     AI 모델에 전달될 원고 내용 (일부): {}...",
                                bookWork.getContent() != null
                                        && bookWork.getContent().length() > 100
                                        ? bookWork.getContent().substring(0, 100)
                                        : bookWork.getContent());

                        // AIServiceSystem 호출 (AI 응답에 keywords도 포함되어야 함)
                        AIServiceSystem.AIResponse aiResponse = aiServiceSystem.callGPTApi(
                                bookWork.getManuscriptId(),
                                publicationInfoCreationRequested.getTitle(),
                                publicationInfoCreationRequested.getSummary(),
                                publicationInfoCreationRequested.getKeywords(),
                                publicationInfoCreationRequested.getAuthorName(),
                                bookWork.getContent());

                        logger.info("##### [Step 5] GPT API 응답 성공적으로 수신 (BookWork ID: {}):",
                                bookWork.getId());
                        logger.info("     AI 응답 - Cover Image URL: {}", aiResponse.getCoverImageUrl());
                        logger.info("     AI 응답 - Ebook URL: {}", aiResponse.getEbookUrl());
                        logger.info("     AI 응답 - Category: {}", aiResponse.getCategory());
                        logger.info("     AI 응답 - Price: {}", aiResponse.getPrice());
                        logger.info("     AI 응답 - AI 생성 Summary: {}", aiResponse.getAiGeneratedSummary());
                        logger.info("     AI 응답 - AI 생성 Keywords: {}", aiResponse.getAiGeneratedKeywords());

                        // BookWork 엔티티에 AI 응답 정보 반영 및 AutoPublished 이벤트 발행
                        bookWork.completeAiProcessing(
                                aiResponse.getCoverImageUrl(),
                                aiResponse.getEbookUrl(),
                                aiResponse.getCategory(),
                                aiResponse.getPrice(),
                                aiResponse.getAiGeneratedSummary(),
                                aiResponse.getAiGeneratedKeywords(),
                                bookWork.getContent()
                        );
                        bookWorkRepository.save(bookWork); // BookWork 상태 변경 후 저장

                        logger.info("##### [Step 6] BookWork 엔티티 (ID: {}) 최종 정보 업데이트 및 AutoPublished 이벤트 발행 완료.",
                                bookWork.getId());
                        logger.info("     최종 Cover Image URL: {}", bookWork.getCoverImageUrl());
                        logger.info("     최종 Ebook URL: {}", bookWork.getEbookUrl());
                        logger.info("     최종 Category: {}", bookWork.getCategory());
                        logger.info("     최종 Price: {}", bookWork.getPrice());
                        logger.info("     최종 Status: {}", bookWork.getStatus());
                        logger.info("##### [Step 6] BookWork 전체 처리 프로세스 완료 (BookWork ID: {}).\n",
                                bookWork.getId());

                        } catch (Exception e) {
                        logger.error("##### [Step 7] AIServiceSystem 호출 중 치명적인 오류 발생 (BookWork ID: {}): {}",
                                bookWork.getId(), e.getMessage(), e);
                        bookWork.setStatus("AI_PROCESSING_FAILED");
                        bookWorkRepository.save(bookWork);
                        logger.error("##### [Step 7] BookWork (ID: {}) 상태가 'AI_PROCESSING_FAILED'로 업데이트됨.",
                                bookWork.getId());
                        }
                });
                if (!bookWorkRepository.findById(publicationInfoCreationRequested.getId()).isPresent()) {
                        logger.warn(
                                "##### [WARNING] PublicationInfoCreationRequested 이벤트에 해당하는 BookWork (ID: {})를 DB에서 찾을 수 없습니다. (이전 이벤트 누락 가능성)",
                                publicationInfoCreationRequested.getId());
                }
                };
        }
}