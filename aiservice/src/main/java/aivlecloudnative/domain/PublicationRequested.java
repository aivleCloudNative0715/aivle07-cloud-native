package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PublicationRequested extends AbstractEvent {
    private Long manuscriptId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorId; // <--- 이 부분을 추가합니다!
    private String authorName;
}