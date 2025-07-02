package aivlecloudnative.infra;

import aivlecloudnative.domain.Author;
import aivlecloudnative.domain.AuthorRepository;
import aivlecloudnative.domain.ApplyAuthorCommand;
import aivlecloudnative.domain.JudgeAuthorCommand;
import aivlecloudnative.domain.AuthorApplied;
import aivlecloudnative.domain.AuthorAccepted;
import aivlecloudnative.domain.AuthorRejected;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // 작가 신청 로직
    // userId는 Command에서 제거되었으므로, 컨트롤러에서 받아와야 합니다.
    public Author applyAuthor(ApplyAuthorCommand command, Long currentUserId) { // currentUserId 파라미터 추가
        // userId를 통해 기존 작가 신청 여부 확인 (재신청 로직 처리)
        Optional<Author> existingAuthorOptional = authorRepository.findByUserId(currentUserId); // command.getUserId() -> currentUserId

        Author authorToSave;
        if (existingAuthorOptional.isPresent()) {
            // 기존 신청이 있는 경우 (재신청)
            authorToSave = existingAuthorOptional.get();
            // 재신청 필드 업데이트 및 상태 초기화
            authorToSave.applyForAuthor(
                command.getAuthorName(),
                command.getBio(),
                command.getRepresentativeWork(),
                command.getPortfolio()
            );
        } else {
            // 새로운 신청인 경우
            authorToSave = new Author(
                currentUserId, // currentUserId 사용
                command.getAuthorEmail(), // authorId로 사용
                command.getAuthorName(),
                command.getBio(),
                command.getRepresentativeWork(),
                command.getPortfolio()
            );
        }

        authorRepository.save(authorToSave);

        // AuthorApplied 이벤트 발행
        AuthorApplied authorApplied = new AuthorApplied(authorToSave);
        authorApplied.publishAfterCommit();

        return authorToSave;
    }

    // 작가 심사 로직 (관리자용)
    public Author judgeAuthor(JudgeAuthorCommand command) {
        // userId로 Author 엔티티 조회
        Author author = authorRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new RuntimeException("Author not found for userId: " + command.getUserId()));

        if (command.getIsApproved()) { // 승인 요청인 경우
            author.acceptAuthor(); // 승인 상태로 변경
            AuthorAccepted authorAccepted = new AuthorAccepted(author);
            authorAccepted.publishAfterCommit();
        } else { // 거부 요청인 경우
            author.rejectAuthor(); // 거부 상태로 변경
            AuthorRejected authorRejected = new AuthorRejected(author);
            authorRejected.publishAfterCommit();
        }

        authorRepository.save(author);
        return author;
    }

    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }
}