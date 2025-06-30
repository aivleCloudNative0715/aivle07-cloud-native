package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import lombok.EqualsAndHashCode;
import java.util.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class SubscriberSignedUp extends AbstractEvent {

    private Long id;
    private String email;
    private String userName;
    private String message;
    private Boolean isKt;
    private Long userId;
}
