package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
import lombok.Data;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorId;

    public PublicationRequested(Manuscript aggregate) {
        super();
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.content = aggregate.getContent();
        this.summary = aggregate.getSummary();
        this.keywords = aggregate.getKeywords();
    }

    public PublicationRequested() {
        super();
    }
}
//>>> DDD / Domain Event
