// src/main/java/aivlecloudnative/domain/PointsGranted.java
package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointsGranted extends AbstractEvent {
    private Long id;
    private Long currentPoints;
    private Long grantedPoints;
    private String userId;
}