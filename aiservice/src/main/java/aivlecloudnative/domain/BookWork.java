package aivlecloudnative.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    private String summary;
    private String keywords;
    private String authorName;
    @Lob // 큰 텍스트 저장을 위해 @Lob 추가
    private String content;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status; // 상태 필드
    private LocalDateTime createdDate;

    // BookWork 객체를 생성하는 팩토리 메서드 (PublicationRequested 이벤트를 기반으로)
    public static BookWork createRequestedBookWork(PublicationRequested publicationRequested) {
        BookWork bookWork = new BookWork();
        bookWork.setManuscriptId(publicationRequested.getManuscriptId());
        bookWork.setTitle(publicationRequested.getTitle());
        bookWork.setSummary(publicationRequested.getSummary());
        bookWork.setKeywords(publicationRequested.getKeywords());
        bookWork.setAuthorName(publicationRequested.getAuthorName());
        bookWork.setContent(publicationRequested.getContent());
        bookWork.setStatus("PUBLICATION_REQUESTED"); // 명확한 초기 상태 (혹은 "PENDING")
        bookWork.setCreatedDate(LocalDateTime.now());
        return bookWork;
    }

    // AI 응답으로 정보 업데이트 및 최종 이벤트 발행
    public void completeAiProcessing(String coverImageUrl, String ebookUrl, String category,
                                    Integer price, String aiGeneratedSummary, String aiGeneratedKeywords, String content) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        this.setSummary(aiGeneratedSummary);
        this.setKeywords(aiGeneratedKeywords);
        this.setContent(content); // <<< content 업데이트
        this.setStatus("AUTO_PUBLISHED");

        AutoPublished autoPublished = new AutoPublished(this);
        autoPublished.publishAfterCommit();
    }

    // AI 처리 실패
    public void failAiProcessing(String errorMessage) {
        this.status = "FAILED";
        // 필요하다면 이벤트 발행 (예: AiProcessingFailed)
        // this.lastGptApiError = errorMessage; // BookWork에 필드 추가해야 함
    }
}