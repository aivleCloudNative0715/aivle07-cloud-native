package aivlecloudnative.infra;

import aivlecloudnative.domain.Book;
import aivlecloudnative.domain.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/books")
@Transactional
public class BookController {

    @Autowired
    BookRepository bookRepository;

    // 특정 ID의 도서 상세 정보 조회 API
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 모든 도서 정보 조회 API (페이징 지원)
    @GetMapping
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    // 베스트셀러 목록 조회 API
    @GetMapping("/bestsellers")
    public List<Book> getBestSellerBooks() {
        // isBestseller가 true인 책들만 조회
        return bookRepository.findByIsBestseller(true);
    }

    // 상위 N개 베스트셀러 목록 조회 API (베스트셀러 중 조회수 상위 N개)
    @GetMapping("/bestsellers/topN")
    public List<Book> getTopNBestSellerBooks(@RequestParam(defaultValue = "10") int n) {
        // isBestseller가 true인 책들을 viewCount 내림차순으로 정렬하여 상위 N개 조회
        Pageable pageable = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "viewCount"));
        return bookRepository.findByIsBestseller(true, pageable).getContent();
    }
}