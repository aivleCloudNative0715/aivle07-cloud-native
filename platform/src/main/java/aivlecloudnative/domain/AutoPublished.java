package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
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
    private String authorName;
}
