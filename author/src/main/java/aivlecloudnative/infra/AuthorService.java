package aivlecloudnative.infra;

import aivlecloudnative.domain.Author;
import org.springframework.stereotype.Service;
import aivlecloudnative.domain.AuthorRepository;
import aivlecloudnative.domain.ApplyAuthorCommand;
import aivlecloudnative.domain.JudgeAuthorCommand;

import java.util.List; // ★ 추가

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author applyAuthor(ApplyAuthorCommand command) {
        Author author = new Author(command.getAuthorName(), command.getAuthorEmail(), command.getPortfolio());
        author.apply();  // 내부에서 AuthorApplied 이벤트 발행
        authorRepository.save(author);
        return author;   // Author 반환
    }

    public Author judgeAuthor(JudgeAuthorCommand command) {
        Author author = authorRepository.findById(command.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if ("ACCEPT".equalsIgnoreCase(command.getJudgement())) {
            author.accept();
        } else {
            author.reject();
        }

        authorRepository.save(author);
        return author;   // Author 반환
    }

    // ★ 작가 전체 목록 반환
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }
}
