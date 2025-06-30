package aivlecloudnative.infra;

import aivlecloudnative.domain.BookView;
import aivlecloudnative.domain.BookViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.annotation.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/bookViews")
@Transactional
public class BookViewController {

    @Autowired
    BookViewRepository bookViewRepository;

    // 모든 BookView 목록 조회 API (페이징 지원)
    @GetMapping
    public Page<BookView> getAllBookViews(Pageable pageable) {
        return bookViewRepository.findAll(pageable);
    }

    // 베스트셀러 목록 조회 API (isbestseller=true 인 도서만 조회, 페이징 지원)
    @GetMapping("/bestsellers")
    public Page<BookView> getBestsellers(Pageable pageable) {
        return bookViewRepository.findByIsbestsellerTrue(pageable);
    }

    // 상위 N개 조회 API >>>
    @GetMapping("/top-ranked-books")
    public Page<BookView> getTopRankedBooks(
        Pageable pageable // Pageable을 직접 받아옴
    ) {
        // 클라이언트에서 page=0, size=10, sort=viewCount,desc 와 같이 요청하면 됨
        // 예: /bookViews/top-ranked-books?page=0&size=10&sort=viewCount,desc
        return bookViewRepository.findByOrderByViewCountDesc(pageable);
    }
}
//>>> Clean Arch / Inbound Adaptor
