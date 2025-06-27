package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ManuscriptSaved extends AbstractEvent {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private Date lastModifiedAt;
    private String summary;
    private String keywords;

    public ManuscriptSaved(Manuscript aggregate) {
        super(aggregate);
    }

    public ManuscriptSaved() {
        super();
    }
}
//>>> DDD / Domain Event
