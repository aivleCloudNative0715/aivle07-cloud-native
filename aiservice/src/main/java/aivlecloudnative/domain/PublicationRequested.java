package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String authorName;
}
