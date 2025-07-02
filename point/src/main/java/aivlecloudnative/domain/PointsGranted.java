package aivlecloudnative.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PointsGranted {
    private String id; // 포인트 고유 ID (UUID 등)
    private Long currentPoints;
    private Long grantedPoints;
    private String userId;
}