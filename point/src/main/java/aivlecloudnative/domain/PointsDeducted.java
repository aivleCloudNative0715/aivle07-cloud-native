// src/main/java/aivlecloudnative/domain/PointsDeducted.java
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
public class PointsDeducted extends AbstractEvent {
    private Long id;
    private Long userId;
    private String bookId;
    private Long deductedPoints;
    private Long currentPoints;
}