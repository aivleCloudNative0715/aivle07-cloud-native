package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class PointsDeducted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long deductedPoints;
    private Integer currentPoints;
    private Long bookId;
}
