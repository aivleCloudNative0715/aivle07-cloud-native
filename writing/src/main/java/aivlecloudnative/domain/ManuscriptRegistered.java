package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ManuscriptRegistered extends AbstractEvent {

    private Long manuscriptId;
    private String authorId;
    private String title;
    private String content;
    private String authorName;
    private String summary;
    private String keywords;
    private String status;

    private Long lastModifiedAt;

    public ManuscriptRegistered(Manuscript aggregate) {
        super(); // 이 시점에 AbstractEvent 내부 timestamp는 무시됨
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.content = aggregate.getContent();
        this.authorName = aggregate.getAuthorName();
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
