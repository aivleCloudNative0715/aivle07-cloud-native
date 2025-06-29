package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ManuscriptRegisterd extends AbstractEvent {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime lastModifiedAt; // Date -> LocalDateTime
    private String summary;
    private String keywords;

    public ManuscriptRegisterd(Manuscript aggregate) {
        super(aggregate);
    }

    public ManuscriptRegisterd() {
        super();
    }
}
//>>> DDD / Domain Event
