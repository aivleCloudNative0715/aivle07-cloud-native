package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorApplied extends AbstractEvent {

    private Long id;
    private String authorEmail;
    private String authorName;
    private String bio;
    private String representativeWork;
    private String portfolio;

    public AuthorApplied(Author aggregate) {
        super(aggregate);
    }

    public AuthorApplied() {
        super();
    }
}
//>>> DDD / Domain Event
