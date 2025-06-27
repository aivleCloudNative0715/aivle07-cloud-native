package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorAccepted extends AbstractEvent {

    private Long id;

    public AuthorAccepted(Author aggregate) {
        super(aggregate);
    }

    public AuthorAccepted() {
        super();
    }
}
//>>> DDD / Domain Event
