package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PointsDeducted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long deductedPoints;
    private Integer currentPoints;

    public PointsDeducted(Point aggregate) {
        super(aggregate);
    }

    public PointsDeducted() {
        super();
    }
}
//>>> DDD / Domain Event
