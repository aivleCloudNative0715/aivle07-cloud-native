package aivlecloudnative.domain;

import lombok.NoArgsConstructor;
import aivlecloudnative.infra.AbstractEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptId;
    private String title;
    private String content;
    private String summary;
    private String authorName;
    private String keywords;
    private String authorId;
}