package aivlecloudnative.domain;
import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class AccessRequestedWithPoints extends AbstractEvent {

    private Long id;
}
