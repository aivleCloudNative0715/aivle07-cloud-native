package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptIdId;
    private String title;
    private String summary;
    private String keywords;
}
