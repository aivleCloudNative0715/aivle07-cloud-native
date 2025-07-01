package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccessRequestedAsSubscriber extends AbstractEvent {

    private String userId;
    private Long bookId;

    public AccessRequestedAsSubscriber(String userId, Long bookId) {
        super();
        this.userId = userId;
        this.bookId = bookId;
    }
}
