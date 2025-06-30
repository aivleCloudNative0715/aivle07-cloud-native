package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false) // AbstractEvent의 필드를 포함하므로 equals/hashCode 생성 시 부모 필드도 고려
public class BookViewed extends AbstractEvent {

    private Long id;
    private Long bookId;
    private Long userId;
    private String title;
    private Long viewCount;
    private String authorName;
    private String summary;

    public BookViewed(Book aggregate, Long userId) {
        super(aggregate);
        this.id = aggregate.getId();
        this.bookId = aggregate.getId();
        this.title = aggregate.getTitle();
        this.viewCount = aggregate.getViewCount();
        this.authorName = aggregate.getAuthorName();
        this.summary = aggregate.getSummary();
        this.userId = userId;
    }

    public BookViewed() {
        super();
    }
}
//>>> DDD / Domain Event
