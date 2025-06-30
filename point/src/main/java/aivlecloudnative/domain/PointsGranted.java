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
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.currentPoints = aggregate.getCurrentPoints();
        this.grantedPoints = 1000L; // 기본 지급 포인트
    }

    public PointsGranted() {
        super();
    }

    public Long getUserId() {
    return this.userId;
    }

    public Integer getCurrentPoints() {
        return this.currentPoints;
    }


}
//>>> DDD / Domain Event
