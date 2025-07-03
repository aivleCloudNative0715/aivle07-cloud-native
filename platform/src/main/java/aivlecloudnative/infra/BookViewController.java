package aivlecloudnative.infra;

import aivlecloudnative.domain.BookView;
import aivlecloudnative.domain.BookViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/bookViews") // /bookViews 경로로 API 제공
@RequiredArgsConstructor // BookViewRepository 주입
public class BookViewController {

    private final BookViewRepository bookViewRepository;

    // 특정 사용자의 도서 열람 기록 조회 API
    @GetMapping("/users/{userId}")
    public List<BookView> getPersonalBookViewHistory(@PathVariable Long userId) {
        return bookViewRepository.findByUserId(userId);
    }

    // 특정 사용자의 특정 도서 열람 기록 조회 API (선택적)
    @GetMapping("/users/{userId}/books/{bookId}")
    public BookView getPersonalBookView(@PathVariable Long userId, @PathVariable Long bookId) {
        return bookViewRepository.findByUserIdAndBookId(userId, bookId).orElse(null);
    }
}