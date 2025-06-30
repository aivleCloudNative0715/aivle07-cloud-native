package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDateTime;
import lombok.*;

//<<< DDD / Domain Event

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ManuscriptRegistered extends AbstractEvent {

    private Long manuscriptId;
    private String authorId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String status;
    private LocalDateTime lastModifiedAt;

    public ManuscriptRegistered(Manuscript aggregate) {
        super();
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.content = aggregate.getContent();
        this.summary = aggregate.getSummary();
        this.keywords = aggregate.getKeywords();
        this.status = aggregate.getStatus();
        this.lastModifiedAt = aggregate.getLastModifiedAt();
    }
    @Override
    public String getEventType() {
        return "ManuscriptRegistered";
    }
}
//>>> DDD / Domain Event