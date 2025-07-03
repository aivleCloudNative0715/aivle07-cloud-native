package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class UserSignedUp extends AbstractEvent {

    private Long userId;
    private String email;
    private String userName;
    private String message;
    private Boolean isKt;
    private Boolean isAuthor;

    public UserSignedUp() {
        super();
    }
}
//>>> DDD / Domain Event
