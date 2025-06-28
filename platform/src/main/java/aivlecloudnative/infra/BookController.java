package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/books")
@Transactional
public class BookController {

    @Autowired
    BookRepository bookRepository;

    // 특정 ID의 도서 상세 정보 조회 API
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        // BookRepository를 통해 Book 애그리거트를 조회
        return bookRepository.findById(id).orElse(null);
    }
}
//>>> Clean Arch / Inbound Adaptor
