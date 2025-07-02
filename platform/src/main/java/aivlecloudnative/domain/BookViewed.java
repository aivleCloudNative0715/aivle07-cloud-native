package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//<<< DDD / Domain Event
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookViewed extends AbstractEvent {
    private Long bookId;
    private String title;
    private String authorName;
    private String summary;
    private String category;
    private String coverImageUrl;
    private Double price;
    private Long totalViewCount; // Book 엔티티의 viewCount
    private Long personalViewCount; // BookView 엔티티의 viewCount
    private String userId;
    private String authorId; // 저자의 email

    public BookViewed(
            Long bookId, String title, String authorName, String summary,
            String category, String coverImageUrl, Double price,
            Long totalViewCount, Long personalViewCount, String userId,
            String authorId) {
        super();
        this.bookId = bookId;
        this.title = title;
        this.authorName = authorName;
        this.summary = summary;
        this.category = category;
        this.coverImageUrl = coverImageUrl;
        this.price = price;
        this.totalViewCount = totalViewCount;
        this.personalViewCount = personalViewCount;
        this.userId = userId;
        this.authorId = authorId;
    }
}
//>>> DDD / Domain Event
