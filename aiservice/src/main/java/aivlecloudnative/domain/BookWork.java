package aivlecloudnative.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.beans.BeanUtils; // AutoPublished 이벤트 복사를 위해 추가

@Entity
@Table(name = "book_work_table")
@Data
@NoArgsConstructor
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manuscriptId;
    private String title;
    private String summary; // AI가 요약 생성하지 않으므로, 이 필드는 원본 요약값을 유지
    private String keywords; // AI가 키워드 생성하지 않으므로, 이 필드는 원본 키워드값을 유지
    private String authorName;
    @Lob // 큰 텍스트 저장을 위해 @Lob 추가
    private String content;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status; // 상태 필드
    private LocalDateTime createdDate;

    // AI가 별도의 요약/키워드를 생성하지 않으므로, 다음 두 필드는 더 이상 필요 없음
    // private String aiGeneratedSummary;
    // private String aiGeneratedKeywords;

    // BookWork 객체를 생성하는 팩토리 메서드 (PublicationRequested 이벤트를 기반으로)
    public static BookWork createRequestedBookWork(PublicationRequested publicationRequested) {
        BookWork bookWork = new BookWork();
        bookWork.setManuscriptId(publicationRequested.getManuscriptId());
        bookWork.setTitle(publicationRequested.getTitle());
        bookWork.setSummary(publicationRequested.getSummary()); // 원본 요약 설정
        bookWork.setKeywords(publicationRequested.getKeywords()); // 원본 키워드 설정
        bookWork.setAuthorName(publicationRequested.getAuthorName());
        bookWork.setContent(publicationRequested.getContent()); // 원고 내용 설정
        bookWork.setStatus("PUBLICATION_REQUESTED"); // 명확한 초기 상태 (혹은 "PENDING")
        bookWork.setCreatedDate(LocalDateTime.now());
        // 필요하다면 여기에서 초기 이벤트 발행
        // bookWork.registerEvent(new InitialBookWorkCreated(bookWork.getId(),
        // bookWork.getTitle()));
        return bookWork;
    }

    // AI 응답으로 정보 업데이트 및 최종 이벤트 발행
    // aiGeneratedSummary, aiGeneratedKeywords, content 인자 제거
    public void completeAiProcessing(String coverImageUrl, String ebookUrl, String category, Integer price) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        // this.setSummary(aiGeneratedSummary); // AI가 요약 생성하지 않으므로 제거
        // this.setKeywords(aiGeneratedKeywords); // AI가 키워드 생성하지 않으므로 제거
        // this.setContent(content); // content는 이미 엔티티에 있으므로 다시 세팅할 필요 없음

        this.setStatus("AUTO_PUBLISHED"); // AI 처리가 완료되고, 자동으로 출판되었다는 의미의 상태

        // AutoPublished 이벤트 발행
        // AutoPublished 생성자에 this를 넘길 경우, AutoPublished DTO의 필드도 BookWork와 일치해야 합니다.
        // AutoPublished DTO에서도 aiGeneratedSummary, aiGeneratedKeywords 필드를 제거했는지 확인하세요.
        AutoPublished autoPublished = new AutoPublished(this); // BookWork 객체의 현재 상태를 기반으로 이벤트 생성
        autoPublished.publishAfterCommit(); // 트랜잭션 커밋 후 이벤트 발행
    }

    // AI 처리 실패
    public void failAiProcessing(String errorMessage) {
        this.status = "AI_PROCESSING_FAILED"; // AI 처리 실패 상태로 변경
        // 필요하다면 이벤트 발행 (예: AiProcessingFailedEvent)
        // registerEvent(new AiProcessingFailedEvent(this.id, errorMessage));
    }
}