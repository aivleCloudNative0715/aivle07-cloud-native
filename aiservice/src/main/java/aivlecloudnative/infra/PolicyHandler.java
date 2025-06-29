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
// import java.util.function.Function; // 현재 Function은 사용하지 않으므로 제거

@Service
@Transactional // 트랜잭션 관리가 필요할 수 있음 (특히 DB 작업 포함 시)
public class PolicyHandler {

    @Autowired
    private BookWorkRepository bookWorkRepository;

    @Autowired
    private AIServiceSystem aiServiceSystem; // AIServiceSystem 주입

    // PublicationRequested 이벤트를 처리하는 Consumer (입력 바인딩)
    // 이 함수는 'publicationRequestedConsumer'라는 이름으로 Spring Context에 빈으로 등록됩니다.
    // application.yml/properties에
    // spring.cloud.stream.function.definition=publicationRequestedConsumer;publicationInfoCreationRequestedConsumer
    // spring.cloud.stream.bindings.publicationRequestedConsumer-in-0.destination=<토픽_이름_PublicationRequested>
    public Consumer<PublicationRequested> publicationRequestedConsumer() {
        return publicationRequested -> {
            System.out.println("\n\n##### publicationRequestedConsumer (PublicationRequested) : "
                    + publicationRequested.toJson() + "\n\n");

            // 1. PublicationRequested 이벤트를 받아 BookWork Aggregate 생성 또는 로드
            // 여기서는 신규 요청이므로 BookWork의 static 메소드를 호출하여 초기화 및 이벤트 발행
            // 이 메소드 안에서 BookWork이 저장되고 PublicationInfoCreationRequested 이벤트가 발행됩니다.
            BookWork.requestNewBookPublication(publicationRequested);
        };
    }

    // PublicationInfoCreationRequested 이벤트를 처리하는 Consumer (입력 바인딩)
    // 이 함수는 'publicationInfoCreationRequestedConsumer'라는 이름으로 Spring Context에 빈으로
    // 등록됩니다.
    // application.yml/properties에
    // spring.cloud.stream.bindings.publicationInfoCreationRequestedConsumer-in-0.destination=<토픽_이름_PublicationInfoCreationRequested>
    public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedConsumer() {
        return publicationInfoCreationRequested -> {
            System.out
                    .println("\n\n##### publicationInfoCreationRequestedConsumer (PublicationInfoCreationRequested) : "
                            + publicationInfoCreationRequested.toJson() + "\n\n");

            // 2. PublicationInfoCreationRequested 이벤트를 받아 AIServiceSystem 호출
            // BookWork ID로 해당 Aggregate를 로드합니다.
            bookWorkRepository.findById(publicationInfoCreationRequested.getId()).ifPresent(bookWork -> {
                try {
                    // AIServiceSystem 호출 (GPT API 연동 로직)
                    // AI 서비스 시스템의 응답을 받아옵니다.
                    AIServiceSystem.AIResponse aiResponse = aiServiceSystem.callGPTApi(
                            publicationInfoCreationRequested.getTitle(),
                            publicationInfoCreationRequested.getSummary(),
                            publicationInfoCreationRequested.getKeywords(),
                            publicationInfoCreationRequested.getAuthorName() // 추가된 필드 사용
                    // 필요한 다른 정보들도 전달 (e.g., manuscript content)
                    );

                    // 3. AI 처리 결과로 BookWork Aggregate의 상태를 업데이트하고 AutoPublished 이벤트 발행
                    bookWork.applyPublicationInfoAndAutoPublish(
                            aiResponse.getCoverImageUrl(),
                            aiResponse.getEbookUrl(),
                            aiResponse.getCategory(),
                            aiResponse.getPrice());
                    // save는 applyPublicationInfoAndAutoPublish 내부에서 처리됨

                } catch (Exception e) {
                    System.err.println("AIServiceSystem 호출 중 오류 발생: " + e.getMessage());
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