package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationInfoCreationRequested;
import aivlecloudnative.domain.PublicationRequested;
import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.external.AIServiceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message; // Spring Messaging Message 타입 import 추가 (필요 시)

import java.time.LocalDateTime;
import java.util.function.Consumer;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import aivlecloudnative.infra.AbstractEvent; // AbstractEvent import 추가
import com.fasterxml.jackson.databind.JsonNode;

@Configuration
@Service
public class PolicyHandler {

    private static final Logger logger = LoggerFactory.getLogger(PolicyHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // ObjectMapper 선언 및 초기화

    @Autowired
    private BookWorkRepository bookWorkRepository;

    @Autowired
    private AIServiceSystem aiServiceSystem;

    /**
     * PublicationRequested 이벤트를 처리하는 컨슈머.
     * 새로운 도서 작업(BookWork) 엔티티를 생성하고 저장한 후,
     * AI 정보 생성을 요청하는 PublicationInfoCreationRequested 이벤트를 발행합니다.
     *
     */
    @Bean
    @Transactional
    public Consumer<Message<PublicationRequested>> publicationRequestedIn() {
        // Consumer의 제네릭 타입을 Message<PublicationRequested>로 변경하여
        // Spring Cloud Stream의 메시징 인터페이스를 명확히 따르도록 합니다.
        // 이렇게 하면 message.getPayload()를 사용하여 실제 PublicationRequested 객체를 얻을 수 있습니다.
        return message -> {
            PublicationRequested event = message.getPayload(); // 여기에서 실제 PublicationRequested 객체를 가져옵니다.

            // 이제 event 객체에서 eventType을 직접 가져올 수 있습니다. (AbstractEvent를 상속받았다고 가정)
            String eventType = event.getEventType(); // PublicationRequested가 AbstractEvent를 상속하고 getEventType()을 가진다면 이렇게 사용

            logger.info("PolicyHandler: Received event from Kafka (Type: {})", eventType);

            // 이벤트 타입 검사 (Optional: Kafka Binder 설정에 따라 필요 없을 수 있음)
            if (!"PublicationRequested".equals(eventType)) {
                logger.warn("Skipping event, type mismatch: Expected PublicationRequested, Got {}", eventType);
                return; // 자신이 처리할 이벤트가 아니면 스킵
            }

            try {
                // BookWork 엔티티 생성 및 필드 설정
                BookWork bookWork = new BookWork();
                bookWork.setManuscriptId(event.getManuscriptId()); // PublicationRequested에 getId()가 있어야 함
                bookWork.setTitle(event.getTitle());
                bookWork.setContent(event.getContent());
                bookWork.setSummary(event.getSummary());
                bookWork.setKeywords(event.getKeywords());
                bookWork.setAuthorId(event.getAuthorId());
                bookWork.setAuthorName(event.getAuthorName());
                bookWork.setStatus("PublicationRequested"); // 초기 상태 설정
                bookWork.setCreatedDate(LocalDateTime.now()); // 생성 시간 설정
                bookWork.setLastModifiedDate(LocalDateTime.now()); // 최종 수정 시간 설정

                // BookWork 저장
                bookWorkRepository.save(bookWork);
                logger.info("##### [Step 2] BookWork 생성 및 초기 상태 설정 완료 (ID: {}).", bookWork.getId());
                System.out.println("First Policy Handler: bookwork created for ID: " + bookWork.getId());

                // PublicationInfoCreationRequested 이벤트 발행
                PublicationInfoCreationRequested publicationInfoCreationRequested = new PublicationInfoCreationRequested();
                publicationInfoCreationRequested.setBookWorkId(bookWork.getId()); // PublicationInfoCreationRequested에 setBookWorkId()가 있어야 함
                publicationInfoCreationRequested.setManuscriptId(event.getManuscriptId()); // PublicationRequested에 getId()가 있어야 함
                publicationInfoCreationRequested.setTitle(event.getTitle());
                publicationInfoCreationRequested.setContent(event.getContent());
                publicationInfoCreationRequested.setSummary(event.getSummary());
                publicationInfoCreationRequested.setKeywords(event.getKeywords());
                publicationInfoCreationRequested.setAuthorId(event.getAuthorId());
                publicationInfoCreationRequested.setAuthorName(event.getAuthorName());
                publicationInfoCreationRequested.publish(); // 이벤트 발행
                logger.info("##### [Step 2-1] PublicationInfoCreationRequested 이벤트 발행 완료 (BookWork ID: {}).",
                        bookWork.getId());

            } catch (Exception e) {
                logger.error("First Policy Handler: Error processing event from Kafka: {}", e.getMessage(), e);
                // 추가적인 에러 처리 (예: DLQ 전송 등)
            }
        };
    }


    /**
     * PublicationInfoCreationRequested 이벤트를 처리하는 컨슈머.
     * 해당 이벤트의 정보를 바탕으로 AI 서비스를 호출하여 추가 정보를 생성하고,
     * BookWork 엔티티를 업데이트한 후 최종 이벤트를 발행합니다.
     *
     * @param event PublicationInfoCreationRequested 타입의 이벤트 객체.
     */
    @Bean
    @Transactional
    public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedIn() {
        return event -> { // Spring Cloud Stream이 이미 페이로드를 PublicationInfoCreationRequested 객체로 역직렬화합니다.

            logger.info("\n\n##### [Step 3] PublicationInfoCreationRequested 이벤트 수신 시작 (Consumer): {}",
                    event.toJson()); // event 객체 사용

            // 이벤트에 포함된 ID로 BookWork 엔티티를 조회
            // PublicationInfoCreationRequested에 getBookWorkId()가 있어야 함
            bookWorkRepository.findById(event.getBookWorkId())
                .ifPresentOrElse(bookWork -> {
                    logger.info("##### [Step 3-1] DB에서 BookWork 엔티티 (ID: {}) 조회 성공. 현재 상태: {}",
                            bookWork.getId(),
                            bookWork.getStatus());

                    try {
                        logger.info("##### [Step 4] AI 서비스 호출을 위한 데이터 준비 중. BookWork ID: {}",
                                bookWork.getId());
                        logger.info("    AI 모델에 전달될 제목: {}",
                                event.getTitle()); // event 객체 사용
                        logger.info("    AI 모델에 전달될 요약: {}",
                                event.getSummary()); // event 객체 사용
                        logger.info("    AI 모델에 전달될 키워드: {}",
                                event.getKeywords()); // event 객체 사용
                        logger.info("    AI 모델에 전달될 저자 ID: {}",
                                event.getAuthorId()); // event 객체 사용
                        logger.info("    AI 모델에 전달될 저자명: {}",
                                event.getAuthorName()); // event 객체 사용
                        logger.info("    AI 모델에 전달될 원고 내용 (일부): {}...",
                                event.getContent() != null && event
                                        .getContent().length() > 100
                                        ? event.getContent()
                                        .substring(0, 100)
                                        : event.getContent()); // event 객체 사용

                        // AI 서비스 호출
                        AIServiceSystem.AIResponse aiResponse = aiServiceSystem
                                .callGPTApi(
                                        event.getManuscriptId(), // event 객체 사용
                                        event.getTitle(), // event 객체 사용
                                        event.getSummary(), // event 객체 사용
                                        event.getKeywords(), // event 객체 사용
                                        event.getAuthorId(), // event 객체 사용
                                        event.getAuthorName(), // event 객체 사용
                                        event.getContent()); // event 객체 사용

                        logger.info("##### [Step 5] AI 서비스 응답 성공적으로 수신 (BookWork ID: {}):",
                                bookWork.getId());
                        logger.info("    AI 응답 - Cover Image URL: {}",
                                aiResponse.getCoverImageUrl());
                        logger.info("    AI 응답 - Ebook URL: {}",
                                aiResponse.getEbookUrl());
                        logger.info("    AI 응답 - Category: {}",
                                aiResponse.getCategory());
                        logger.info("    AI 응답 - Price: {}", aiResponse.getPrice());

                        // BookWork 엔티티에 AI 응답 정보 업데이트 및 상태 변경 (completeAiProcessing 메서드 호출)
                        bookWork.completeAiProcessing( // BookWork 도메인에 이 메서드가 정의되어 있어야 합니다.
                                aiResponse.getCoverImageUrl(),
                                aiResponse.getEbookUrl(),
                                aiResponse.getCategory(),
                                aiResponse.getPrice());
                        bookWorkRepository.save(bookWork); // 업데이트된 BookWork 저장

                        logger.info("##### [Step 6] BookWork 엔티티 (ID: {}) 최종 정보 업데이트 완료. AutoPublished 이벤트 발행 준비 중.",
                                bookWork.getId());

                        // --- AutoPublished 이벤트 발행 로직 시작 ---
                        AutoPublished autoPublishedEvent = new AutoPublished();
                        autoPublishedEvent.setId(bookWork.getId());
                        autoPublishedEvent.setManuscriptId(bookWork.getManuscriptId());
                        autoPublishedEvent.setTitle(bookWork.getTitle());
                        autoPublishedEvent.setAuthorId(bookWork.getAuthorId());
                        autoPublishedEvent.setAuthorName(bookWork.getAuthorName());
                        autoPublishedEvent.setCoverImageUrl(bookWork.getCoverImageUrl());
                        autoPublishedEvent.setEbookUrl(bookWork.getEbookUrl());
                        autoPublishedEvent.setCategory(bookWork.getCategory());
                        autoPublishedEvent.setPrice(bookWork.getPrice());
                        autoPublishedEvent.publish(); // AutoPublished 이벤트 발행

                        logger.info("##### [Step 6-1] AutoPublished 이벤트 발행 완료 (BookWork ID: {}).",
                                bookWork.getId());
                        // --- AutoPublished 이벤트 발행 로직 끝 ---

                        logger.info("##### [Step 6] BookWork 전체 처리 프로세스 완료 (BookWork ID: {}).\n",
                                bookWork.getId());

                    } catch (Exception e) {
                        logger.error("##### [Step 7] AI 서비스 호출 중 치명적인 오류 발생 (BookWork ID: {}): {}",
                                bookWork.getId(), e.getMessage(), e);
                        bookWork.failAiProcessing(e.getMessage()); // BookWork 도메인에 이 메서드가 정의되어 있어야 합니다.
                        bookWorkRepository.save(bookWork); // 실패 상태 업데이트 저장
                        logger.error("##### [Step 7] BookWork (ID: {}) 상태가 'AI_PROCESSING_FAILED'로 업데이트됨.",
                                bookWork.getId());
                    }
                }, () -> {
                    // BookWork을 찾지 못한 경우 (이벤트가 너무 빨리 오거나 이전 단계 오류)
                    logger.warn(
                            "##### [WARNING] PublicationInfoCreationRequested 이벤트에 해당하는 BookWork (ID: {})를 DB에서 찾을 수 없습니다. (이전 이벤트 처리 누락 또는 타이밍 문제)",
                            event.getBookWorkId()); // PublicationInfoCreationRequested에 getBookWorkId()가 있어야 함
                });
        };
    }
}