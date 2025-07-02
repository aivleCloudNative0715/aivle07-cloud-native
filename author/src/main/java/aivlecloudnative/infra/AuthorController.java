package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.List;

@RestController
@Transactional
@RequestMapping("/author-api") // 경로를 변경하여 충돌 방지!
public class AuthorController {

    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // 작가 신청
    @PostMapping("/apply")
    public Author applyAuthor(@RequestBody ApplyAuthorCommand command) {
        return authorService.applyAuthor(command);
    }

    // 작가 심사(승인/거부)
    @PostMapping("/judge")
    public Author judgeAuthor(@RequestBody JudgeAuthorCommand command) {
        return authorService.judgeAuthor(command);
    }

    // 작가 전체 목록 조회
    @GetMapping
    public List<Author> getAuthors() {
        return authorService.getAuthors();
    }
}
