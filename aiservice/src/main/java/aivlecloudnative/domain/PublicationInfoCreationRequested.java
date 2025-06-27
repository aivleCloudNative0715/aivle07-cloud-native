package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PublicationInfoCreationRequested extends AbstractEvent {

    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String status;

    public PublicationInfoCreationRequested(BookWork aggregate) {
        super(aggregate);
    }

    public PublicationInfoCreationRequested() {
        super();
    }
}
//>>> DDD / Domain Event
