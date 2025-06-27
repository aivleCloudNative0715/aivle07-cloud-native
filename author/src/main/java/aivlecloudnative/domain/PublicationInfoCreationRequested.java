package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class PublicationInfoCreationRequested extends AbstractEvent {

    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String status;
}
