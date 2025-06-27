package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class SubscriberSignedUp extends AbstractEvent {

    private Long id;
    private String email;
    private String userName;
    private String message;
    private Boolean isKt;
    private Long userId;

    public SubscriberSignedUp(User aggregate) {
        super(aggregate);
    }

    public SubscriberSignedUp() {
        super();
    }
}
//>>> DDD / Domain Event
