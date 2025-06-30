package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AccessRequestedAsSubscriber extends AbstractEvent {

    private Long userId;
    private Long bookId;

    public AccessRequestedAsSubscriber(User aggregate) {
        super(aggregate);
    }

    public AccessRequestedAsSubscriber() {
        super();
    }
}
//>>> DDD / Domain Event
