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
    private String summary;
    private String keywords;

    public PublicationRequested(Manuscript aggregate) {
        super(aggregate);
    }

    public PublicationRequested() {
        super();
    }
}
//>>> DDD / Domain Event
