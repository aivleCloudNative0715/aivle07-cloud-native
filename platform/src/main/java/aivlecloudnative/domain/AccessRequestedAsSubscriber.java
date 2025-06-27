package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class AccessRequestedAsSubscriber extends AbstractEvent {

    private Long id;
    private Long bookId;
}
