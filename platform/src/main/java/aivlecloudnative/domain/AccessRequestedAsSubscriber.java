package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class AccessRequestedAsSubscriber extends AbstractEvent {
    private Long id;
    private Long bookId;
}
