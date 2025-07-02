package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import java.util.List;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookWorks")
public class BookWorkController {

    // 로거 클래스 이름 수정
    private static final Logger log = LoggerFactory.getLogger(BookWorkController.class);
    private final BookWorkRepository bookWorkRepository;

    public BookWorkController(BookWorkRepository bookWorkRepository) {
        this.bookWorkRepository = bookWorkRepository;
    }

    // authorId로 BookWork 조회 API (단일 BookWork 반환 가정)
    @GetMapping("/{authorId}")
    public ResponseEntity<List<BookWork>> getBookWorksByAuthorId(@PathVariable String authorId) { // 메서드명 변경 (복수형)
        log.info("Attempting to retrieve BookWorks for authorId: {}", authorId);

        // bookWorkRepository.findByAuthorId(authorId)가 List<BookWork>를 반환한다고 가정
        List<BookWork> books = bookWorkRepository.findByAuthorId(authorId);

        if (!books.isEmpty()) {
            log.info("Successfully retrieved {} BookWorks for authorId: {}", books.size(), authorId);
            return new ResponseEntity<>(books, HttpStatus.OK); // 조회된 목록 전체 반환
        } else {
            log.warn("No BookWorks found for authorId: {}", authorId);
            // 책이 없을 경우, 404 Not Found 대신 빈 리스트와 200 OK를 반환하는 것이 RESTful API에서 더 흔함
            // 클라이언트는 빈 배열을 받고 '책이 없음'을 인지할 수 있습니다.
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            // 만약 404를 원한다면:
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
