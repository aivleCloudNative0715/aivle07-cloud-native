package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ManuscriptSaved extends AbstractEvent {

    private Long manuscriptId;
    private String authorId;
    private String title;
    private String status;
    private LocalDateTime lastModifiedAt;

    public ManuscriptSaved(Manuscript aggregate) {
        super();
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.status = aggregate.getStatus();
        this.lastModifiedAt = aggregate.getLastModifiedAt();
    }

    public ManuscriptSaved() {
        super();
    }
}

