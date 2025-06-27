package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PointsDeducted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long deductedPoints;
    private Integer currentPoints;
}
