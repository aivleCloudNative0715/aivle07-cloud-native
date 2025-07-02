package aivlecloudnative.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; // ToString 어노테이션을 명시적으로 import (선택 사항)
import java.time.LocalDateTime;
// org.springframework.beans.BeanUtils; // 현재 코드에서 직접 사용되지는 않지만, 다른 곳에서 사용할 수 있음

@Entity
@Table(name = "book_work_table")
@Data
@NoArgsConstructor
// ToString에서 content와 같은 긴 필드는 제외하여 로그를 깔끔하게 유지합니다.
@ToString(exclude = { "content" })
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String authorId;
    private String authorName;
    @Lob
    private String content;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String failReason;

    // BookWork 객체를 생성하는 팩토리 메서드 (PublicationRequested 이벤트를 기반으로)
    public static BookWork createRequestedBookWork(PublicationRequested publicationRequested) {
        BookWork bookWork = new BookWork();
        bookWork.setManuscriptId(publicationRequested.getManuscriptId());
        bookWork.setTitle(publicationRequested.getTitle());
        bookWork.setSummary(publicationRequested.getSummary());
        bookWork.setKeywords(publicationRequested.getKeywords());
        bookWork.setAuthorId(publicationRequested.getAuthorId());
        bookWork.setAuthorName(publicationRequested.getAuthorName());
        bookWork.setContent(publicationRequested.getContent());
        bookWork.setStatus("PUBLICATION_REQUESTED");
        bookWork.setCreatedDate(LocalDateTime.now());
        bookWork.setLastModifiedDate(LocalDateTime.now()); // <-- 생성 시점 업데이트
        return bookWork;
    }

    // AI 응답으로 정보 업데이트 및 최종 이벤트 발행
    public void completeAiProcessing(String coverImageUrl, String ebookUrl, String category, Integer price) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        this.setStatus("AUTO_PUBLISHED");
        this.setLastModifiedDate(LocalDateTime.now()); // <-- 완료 시점 업데이트

        AutoPublished autoPublished = new AutoPublished(this);
        autoPublished.publishAfterCommit();
    }

    // AI 처리 실패
    public void failAiProcessing(String errorMessage) {
        this.status = "AI_PROCESSING_FAILED";
        this.failReason = errorMessage; // <-- 실패 사유 저장
        this.setLastModifiedDate(LocalDateTime.now()); // <-- 실패 시점 업데이트
        // 필요하다면 이벤트 발행 (예: AiProcessingFailedEvent)
        // registerEvent(new AiProcessingFailedEvent(this.id, errorMessage));
    }
}