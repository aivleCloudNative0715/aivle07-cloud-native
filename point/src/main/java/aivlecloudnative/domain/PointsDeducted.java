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
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.currentPoints = aggregate.getCurrentPoints();
        this.deductedPoints = 500L; // 예시: 기본 차감 포인트
    }

    public PointsDeducted() {
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
