// src/main/java/aivlecloudnative/domain/AccessRequestedWithPoints.java
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
public class AccessRequestedWithPoints extends AbstractEvent {
    private String userId;
    private String bookId;
}