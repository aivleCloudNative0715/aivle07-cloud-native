package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class SubscriberSignedUp extends AbstractEvent {

    private Long id;
    private String email;
    private String userName;
    private String message;
    private Boolean isKt;
    private Long userId;
}
