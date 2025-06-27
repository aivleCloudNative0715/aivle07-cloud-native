package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AccessRequestedWithPoints extends AbstractEvent {

    private Long id;

    public AccessRequestedWithPoints(User aggregate) {
        super(aggregate);
    }

    public AccessRequestedWithPoints() {
        super();
    }
}
//>>> DDD / Domain Event
