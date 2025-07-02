// src/main/java/aivlecloudnative/domain/PointsGranted.java
package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PointsGranted extends AbstractEvent {
    private Long id; // 포인트 고유 ID
    private Long currentPoints;
    private Long grantedPoints;
    private String userId;
}