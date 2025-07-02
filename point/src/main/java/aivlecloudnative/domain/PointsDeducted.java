package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PointsDeducted extends AbstractEvent {
    private Long id; // 포인트 차감된 Point 엔티티의 고유 ID
    private String userId;
    private String bookId;
    private Long deductedPoints; // 차감된 포인트 양
    private Long currentPoints;  // 차감 후 사용자의 현재 포인트
}