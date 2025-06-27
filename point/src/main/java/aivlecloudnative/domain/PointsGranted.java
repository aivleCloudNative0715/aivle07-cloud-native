package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PointsGranted extends AbstractEvent {

    private Long id;
    private Integer currentPoints;
    private Long grantedPoints;
    private Long userId;

    public PointsGranted(Point aggregate) {
        super(aggregate);
    }

    public PointsGranted() {
        super();
    }
}
//>>> DDD / Domain Event
