package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationInfoCreationRequested; // 이 이벤트는 여기서 발행
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/bookWorks")
@Transactional
public class BookWorkController {

    @Autowired
    private BookWorkRepository bookWorkRepository;

    @Autowired
    private StreamBridge streamBridge; // StreamBridge 주입

    // BookWork 등록 API
    @PostMapping
    public ResponseEntity<BookWork> createBookWork(@RequestBody BookWork bookWork) { // @RequestBody는 BookWork 엔티티 자체를 받도록
        try {
            // BookWork 상태 초기화
            bookWork.setStatus("REQUESTED"); // 초기 상태 (PublicationRequested와 구분)
            bookWork.setCreatedDate(LocalDateTime.now());

            // DB에 BookWork 저장
            BookWork savedBookWork = bookWorkRepository.save(bookWork);

            // PublicationInfoCreationRequested 이벤트를 직접 발행
            PublicationInfoCreationRequested event = new PublicationInfoCreationRequested(savedBookWork);

            // StreamBridge를 사용하여 이벤트 발행 (application.yml의 바인딩 이름 사용)
            // "publicationInfoCreationRequested-out-0" 바인딩으로 전송
            boolean success = streamBridge.send("publicationInfoCreationRequested-out-0",
                    MessageBuilder.withPayload(event).build());

            if (!success) {
                System.err.println("Failed to send PublicationInfoCreationRequested event for manuscriptId: "
                        + savedBookWork.getManuscriptId()); // <<< getManuscriptIdId -> getManuscriptId
            } else {
                System.out.println("##### [Controller] PublicationInfoCreationRequested 이벤트 발행 완료: " + event.toJson());
            }

            return new ResponseEntity<>(savedBookWork, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error creating BookWork: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // BookWork 조회 (필요하다면 추가)
    @GetMapping("/{id}")
    public ResponseEntity<BookWork> getBookWork(@PathVariable Long id) {
        return bookWorkRepository.findById(id)
                .map(bookWork -> new ResponseEntity<>(bookWork, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 모든 BookWork 조회 (페이징 포함)
    @GetMapping
    public ResponseEntity<Page<BookWork>> getAllBookWorks(Pageable pageable) {
        Page<BookWork> bookWorks = bookWorkRepository.findAll(pageable);
        return new ResponseEntity<>(bookWorks, HttpStatus.OK);
    }
}