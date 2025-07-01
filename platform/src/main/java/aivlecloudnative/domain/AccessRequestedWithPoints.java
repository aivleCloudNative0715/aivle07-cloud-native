package aivlecloudnative.domain; // 또는 aivlecloudnative.event

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccessRequestedWithPoints extends AbstractEvent {

    private String userId;
    private Long bookId;

    public AccessRequestedWithPoints(String userId, Long bookId) {
        super();
        this.userId = userId;
        this.bookId = bookId;
    }
}