package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationInfoCreationRequested; // 이 이벤트는 여기서 발행
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.stream.function.StreamBridge; // StreamBridge 주입
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime; // LocalDateTime 임포트

@RestController
@RequestMapping("/bookWorks")
public class BookWorkController {

    @Autowired
    private BookWorkRepository bookWorkRepository;

    @Autowired
    private StreamBridge streamBridge; // StreamBridge 주입

    @PostMapping
    public ResponseEntity<BookWork> createBookWork(@RequestBody BookWork bookWork) {
        try {
            // BookWork 상태 초기화 (필요하다면)
            bookWork.setStatus("PublicationInfoCreationRequested"); // 초기 상태 설정
            bookWork.setCreatedDate(LocalDateTime.now()); // 생성 시간 설정

            // DB에 BookWork 저장
            BookWork savedBookWork = bookWorkRepository.save(bookWork);

            // PublicationInfoCreationRequested 이벤트를 직접 발행
            PublicationInfoCreationRequested event = new PublicationInfoCreationRequested(savedBookWork);

            // StreamBridge를 사용하여 이벤트 발행 (application.yml의 바인딩 이름 사용)
            // "publicationInfoCreationRequested-out-0" 바인딩으로 전송
            boolean success = streamBridge.send("publicationInfoCreationRequested-out-0",
                    MessageBuilder.withPayload(event).build());

            if (!success) {
                // 이벤트 발행 실패 시 로깅 또는 예외 처리
                System.err.println("Failed to send PublicationInfoCreationRequested event for manuscriptIdId: "
                        + savedBookWork.getManuscriptIdId());
            } else {
                System.out.println("##### [Controller] PublicationInfoCreationRequested 이벤트 발행 완료: " + event.toJson());
            }

            return new ResponseEntity<>(savedBookWork, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error creating BookWork: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 출력
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}