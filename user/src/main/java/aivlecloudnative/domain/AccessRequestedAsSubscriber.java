package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AccessRequestedAsSubscriber extends AbstractEvent {

    private Long id;
    private Long bookId;

    public AccessRequestedAsSubscriber(User aggregate) {
        super(aggregate);
    }

    public AccessRequestedAsSubscriber() {
        super();
    }
}
//>>> DDD / Domain Event
