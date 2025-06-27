package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class AutoPublished extends AbstractEvent {

    private Long id;
    private String title;
    private Long manuscriptId;
    private String summary;
    private String keywords;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status;
    private String summery;
}
