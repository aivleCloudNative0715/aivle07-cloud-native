package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoPublished extends AbstractEvent {

    private Long id;
    private String title;
    private Long manuscriptId;
    private String content;
    private String summary;
    private String keywords;
    private String authorId;
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    private String status;

}