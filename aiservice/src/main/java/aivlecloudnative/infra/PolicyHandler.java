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
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import jakarta.transaction.Transactional;

@Configuration
@Service
public class PolicyHandler {

        private static final Logger logger = LoggerFactory.getLogger(PolicyHandler.class);

        @Autowired
        private BookWorkRepository bookWorkRepository;

        @Autowired
        private AIServiceSystem aiServiceSystem;

        /**
         * PublicationRequested 이벤트를 처리하는 컨슈머.
         * 새로운 도서 작업(BookWork) 엔티티를 생성하고 저장한 후,
         * AI 정보 생성을 요청하는 PublicationInfoCreationRequested 이벤트를 발행합니다.
         */
        // !!! 이 @Bean 메서드 전체를 주석 처리합니다. !!!
        // @Bean
        // @Transactional
        // public Consumer<PublicationRequested> publicationRequestedConsumer() {
        // return publicationRequested -> {
        // logger.info("\n\n##### [Step 1] PublicationRequested 이벤트 수신 시작 (Consumer):
        // {}",
        // publicationRequested.toJson());

        // BookWork bookWork = BookWork.createRequestedBookWork(publicationRequested);
        // bookWorkRepository.save(bookWork);

        // logger.info(
        // "##### [Step 2] BookWork 생성 및 초기 상태 설정 완료 (ID: {}).",
        // bookWork.getId());

        // PublicationInfoCreationRequested publicationInfoCreationRequested = new
        // PublicationInfoCreationRequested();
        // publicationInfoCreationRequested.setId(bookWork.getId());
        // publicationInfoCreationRequested.setManuscriptId(bookWork.getManuscriptId());
        // publicationInfoCreationRequested.setTitle(bookWork.getTitle());
        // publicationInfoCreationRequested.setSummary(bookWork.getSummary());
        // publicationInfoCreationRequested.setKeywords(bookWork.getKeywords());
        // publicationInfoCreationRequested.setAuthorId(bookWork.getAuthorId());
        // publicationInfoCreationRequested.setAuthorName(bookWork.getAuthorName());
        // publicationInfoCreationRequested.setContent(bookWork.getContent());

        // publicationInfoCreationRequested.publish();
        // logger.info("##### [Step 2-1] PublicationInfoCreationRequested 이벤트 발행 완료
        // (BookWork ID: {}).",
        // bookWork.getId());
        // };
        // }

        /**
         * PublicationInfoCreationRequested 이벤트를 처리하는 컨슈머.
         * AI 서비스를 호출하여 출판 관련 정보를 생성하고, BookWork 엔티티를 업데이트한 후 AutoPublished 이벤트를 발행합니다.
         */
        @Bean
        @Transactional
        public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedConsumer() {
                return publicationInfoCreationRequested -> {
                        logger.info("\n\n##### [Step 3] PublicationInfoCreationRequested 이벤트 수신 시작 (Consumer): {}",
                                        publicationInfoCreationRequested.toJson());

                        bookWorkRepository.findById(publicationInfoCreationRequested.getId())
                                        .ifPresentOrElse(bookWork -> {
                                                logger.info("##### [Step 3-1] DB에서 BookWork 엔티티 (ID: {}) 조회 성공. 현재 상태: {}",
                                                                bookWork.getId(),
                                                                bookWork.getStatus());

                                                try {
                                                        logger.info("##### [Step 4] AI 서비스 호출을 위한 데이터 준비 중. BookWork ID: {}",
                                                                        bookWork.getId());
                                                        logger.info("       AI 모델에 전달될 제목: {}",
                                                                        publicationInfoCreationRequested.getTitle());
                                                        logger.info("       AI 모델에 전달될 요약: {}",
                                                                        publicationInfoCreationRequested.getSummary());
                                                        logger.info("       AI 모델에 전달될 키워드: {}",
                                                                        publicationInfoCreationRequested.getKeywords());
                                                        logger.info("       AI 모델에 전달될 저자 ID: {}",
                                                                        publicationInfoCreationRequested.getAuthorId());
                                                        logger.info("       AI 모델에 전달될 저자명: {}",
                                                                        publicationInfoCreationRequested
                                                                                        .getAuthorName());
                                                        logger.info("       AI 모델에 전달될 원고 내용 (일부): {}...",
                                                                        bookWork.getContent() != null && bookWork
                                                                                        .getContent().length() > 100
                                                                                                        ? bookWork.getContent()
                                                                                                                        .substring(0, 100)
                                                                                                        : bookWork.getContent());

                                                        AIServiceSystem.AIResponse aiResponse = aiServiceSystem
                                                                        .callGPTApi(
                                                                                        publicationInfoCreationRequested
                                                                                                        .getManuscriptId(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getTitle(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getSummary(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getKeywords(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getAuthorId(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getAuthorName(),
                                                                                        publicationInfoCreationRequested
                                                                                                        .getContent());

                                                        logger.info("##### [Step 5] AI 서비스 응답 성공적으로 수신 (BookWork ID: {}):",
                                                                        bookWork.getId());
                                                        logger.info("       AI 응답 - Cover Image URL: {}",
                                                                        aiResponse.getCoverImageUrl());
                                                        logger.info("       AI 응답 - Ebook URL: {}",
                                                                        aiResponse.getEbookUrl());
                                                        logger.info("       AI 응답 - Category: {}",
                                                                        aiResponse.getCategory());
                                                        logger.info("       AI 응답 - Price: {}", aiResponse.getPrice());

                                                        bookWork.completeAiProcessing(
                                                                        aiResponse.getCoverImageUrl(),
                                                                        aiResponse.getEbookUrl(),
                                                                        aiResponse.getCategory(),
                                                                        aiResponse.getPrice());
                                                        bookWorkRepository.save(bookWork);

                                                        logger.info("##### [Step 6] BookWork 엔티티 (ID: {}) 최종 정보 업데이트 및 AutoPublished 이벤트 발행 완료.",
                                                                        bookWork.getId());
                                                        logger.info("       최종 Cover Image URL: {}",
                                                                        bookWork.getCoverImageUrl());
                                                        logger.info("       최종 Ebook URL: {}", bookWork.getEbookUrl());
                                                        logger.info("       최종 Category: {}", bookWork.getCategory());
                                                        logger.info("       최종 Price: {}", bookWork.getPrice());
                                                        logger.info("       최종 Status: {}", bookWork.getStatus());
                                                        logger.info("##### [Step 6] BookWork 전체 처리 프로세스 완료 (BookWork ID: {}).\n",
                                                                        bookWork.getId());

                                                } catch (Exception e) {
                                                        logger.error("##### [Step 7] AI 서비스 호출 중 치명적인 오류 발생 (BookWork ID: {}): {}",
                                                                        bookWork.getId(), e.getMessage(), e);
                                                        bookWork.failAiProcessing(e.getMessage());
                                                        bookWorkRepository.save(bookWork);
                                                        logger.error("##### [Step 7] BookWork (ID: {}) 상태가 'AI_PROCESSING_FAILED'로 업데이트됨.",
                                                                        bookWork.getId());
                                                }
                                        }, () -> {
                                                logger.warn(
                                                                "##### [WARNING] PublicationInfoCreationRequested 이벤트에 해당하는 BookWork (ID: {})를 DB에서 찾을 수 없습니다. (이전 이벤트 처리 누락 또는 타이밍 문제)",
                                                                publicationInfoCreationRequested.getId());
                                        });
                };
        }
}