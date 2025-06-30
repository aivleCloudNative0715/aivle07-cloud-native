package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationRequested;
import aivlecloudnative.domain.PublicationInfoCreationRequested;
import aivlecloudnative.external.AIServiceSystem; // AIServiceSystem 인터페이스 또는 클라이언트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리
import java.util.function.Consumer;
import org.slf4j.Logger; // Logger 임포트
import org.slf4j.LoggerFactory; // LoggerFactory 임포트

@Service
@Transactional // 트랜잭션 관리가 필요할 수 있음 (특히 DB 작업 포함 시)
public class PolicyHandler {

    private static final Logger logger = LoggerFactory.getLogger(PolicyHandler.class); // Logger 인스턴스 생성

    @Autowired
    private BookWorkRepository bookWorkRepository;

    @Autowired
    private AIServiceSystem aiServiceSystem; // AIServiceSystem 주입

    // PublicationRequested 이벤트를 처리하는 Consumer (입력 바인딩)
    // 이 함수는 'publicationRequestedConsumer'라는 이름으로 Spring Context에 등록됩니다.
    // application.yml/properties에
    // spring.cloud.stream.function.definition=publicationRequestedConsumer;publicationInfoCreationRequestedConsumer
    // spring.cloud.stream.bindings.publicationRequestedConsumer-in-0.destination=<토픽_이름_PublicationRequested>
    public Consumer<PublicationRequested> publicationRequestedConsumer() {
        return publicationRequested -> {
            // 초기 PublicationRequested 이벤트 수신 로그
            logger.info("\n\n##### publicationRequestedConsumer (PublicationRequested) : "
                    + publicationRequested.toJson() + "\n\n");

            // 1. PublicationRequested 이벤트를 받아 BookWork Aggregate 생성 또는 로드
            // 여기서는 신규 요청이므로 BookWork의 static 메소드를 호출하여 초기화 및 이벤트 발행
            // 이 메소드 안에서 BookWork이 저장되고 PublicationInfoCreationRequested 이벤트가 발행됩니다.
            BookWork.requestNewBookPublication(publicationRequested);
        };
    }

    // PublicationInfoCreationRequested 이벤트를 처리하는 Consumer (입력 바인딩)
    // 이 함수는 'publicationInfoCreationRequestedConsumer'라는 이름으로 Spring Context에 등록
    // application.yml/properties에
    // spring.cloud.stream.bindings.publicationInfoCreationRequestedConsumer-in-0.destination=<토픽_이름_PublicationInfoCreationRequested>
    public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedConsumer() {
        return publicationInfoCreationRequested -> {
            // PublicationInfoCreationRequested 이벤트 수신 로그
            logger.info("\n\n##### publicationInfoCreationRequestedConsumer (PublicationInfoCreationRequested) : "
                    + publicationInfoCreationRequested.toJson() + "\n\n");

            // 2. PublicationInfoCreationRequested 이벤트를 받아 AIServiceSystem 호출
            // BookWork ID로 해당 Aggregate를 로드합니다.
            bookWorkRepository.findById(publicationInfoCreationRequested.getId()).ifPresent(bookWork -> {
                try {
                    // GPT API 호출 전 로그
                    logger.info("### Calling GPT API for BookWork ID: {}", bookWork.getId());
                    AIServiceSystem.AIResponse aiResponse = aiServiceSystem.callGPTApi(
                            publicationInfoCreationRequested.getTitle(),
                            publicationInfoCreationRequested.getSummary(),
                            publicationInfoCreationRequested.getKeywords(),
                            publicationInfoCreationRequested.getAuthorName()
                    // 필요한 다른 정보들도 전달 (e.g., manuscript content)
                    );

                    // ⭐️ GPT API 응답 결과 콘솔 출력
                    logger.info("### GPT API Response Received for BookWork ID {}:", bookWork.getId());
                    logger.info("    Cover Image URL: {}", aiResponse.getCoverImageUrl());
                    logger.info("    Ebook URL: {}", aiResponse.getEbookUrl());
                    logger.info("    Category: {}", aiResponse.getCategory());
                    logger.info("    Price: {}", aiResponse.getPrice());

                    // 3. AI 처리 결과로 BookWork Aggregate의 상태를 업데이트하고 AutoPublished 이벤트 발행
                    bookWork.applyPublicationInfoAndAutoPublish(
                            aiResponse.getCoverImageUrl(),
                            aiResponse.getEbookUrl(),
                            aiResponse.getCategory(),
                            aiResponse.getPrice());
                    // save는 applyPublicationInfoAndAutoPublish 내부에서 처리됨

                    // ⭐️ BookWork 업데이트 후 최종 상태 콘솔 출력
                    logger.info("### BookWork Updated and AutoPublished for ID {}:", bookWork.getId());
                    logger.info("    Final Cover Image URL: {}", bookWork.getCoverImageUrl());
                    logger.info("    Final Ebook URL: {}", bookWork.getEbookUrl());
                    logger.info("    Final Category: {}", bookWork.getCategory());
                    logger.info("    Final Price: {}", bookWork.getPrice());
                    logger.info("    Final Status: {}", bookWork.getStatus());
                    logger.info("### BookWork processing completed for ID {}.\n", bookWork.getId());

                } catch (Exception e) {
                    // 오류 발생 시 로그 및 상태 업데이트
                    logger.error("##### AIServiceSystem 호출 중 오류 발생 for BookWork ID {}: {}", bookWork.getId(),
                            e.getMessage(), e);
                    // ⚠️ 중요: 에러 처리 로직 (보상 트랜잭션, 실패 이벤트 발행, 알림 등)
                    // 예를 들어, BookWork의 상태를 "AI_PROCESSING_FAILED" 등으로 업데이트하고
                    // FailureEvent를 발행하여 다른 시스템에 알릴 수 있습니다.
                    bookWork.setStatus("AI_PROCESSING_FAILED");
                    bookWorkRepository.save(bookWork);
                    // new AiProcessingFailed(bookWork.getId(),
                    // e.getMessage()).publishAfterCommit();
                }
            });
        };
    }
}