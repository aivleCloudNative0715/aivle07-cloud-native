package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptIdId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorName;

    public PublicationRequested(Manuscript aggregate) {
        super(aggregate);
        this.manuscriptIdId = aggregate.getManuscriptIdId();
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
