package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class AuthorAccepted extends AbstractEvent {

    private Long id;

    public AuthorAccepted(Long id) {
        this.id = id;
    }
}

