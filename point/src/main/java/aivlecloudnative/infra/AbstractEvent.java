package aivlecloudnative.infra;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public abstract class AbstractEvent {
    private String eventType;
    private LocalDateTime timestamp;

    public AbstractEvent() {
        this.eventType = this.getClass().getSimpleName();
        this.timestamp = LocalDateTime.now();
    }
}