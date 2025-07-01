import aivlecloudnative.domain.Author;
import org.springframework.stereotype.Service;
import aivlecloudnative.domain.AuthorRepository;
import aivlecloudnative.domain.ApplyAuthorCommand;
import aivlecloudnative.domain.JudgeAuthorCommand;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void applyAuthor(ApplyAuthorCommand command) {
        Author author = new Author(command.getAuthorName(), command.getAuthorEmail(), command.getPortfolio());
        author.apply();  // 내부에서 AuthorApplied 이벤트 발행
        authorRepository.save(author);
    }

    public void judgeAuthor(JudgeAuthorCommand command) {
        Author author = authorRepository.findById(command.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if ("ACCEPT".equalsIgnoreCase(command.getJudgement())) {
            author.accept();
        } else {
            author.reject();
        }

        authorRepository.save(author);
    }
}

