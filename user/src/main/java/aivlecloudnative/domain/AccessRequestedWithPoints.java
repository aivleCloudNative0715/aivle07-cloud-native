package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AccessRequestedWithPoints extends AbstractEvent {

    private Long userId;
    private Long bookId;

    public AccessRequestedWithPoints(User aggregate) {
        super(aggregate);
    }

    public AccessRequestedWithPoints() {
        super();
    }
}
//>>> DDD / Domain Event
