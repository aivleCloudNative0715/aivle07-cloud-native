package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor; // Lombok의 @NoArgsConstructor는 이미 기본 생성자를 추가합니다.
import aivlecloudnative.infra.AbstractEvent;
import com.fasterxml.jackson.core.JsonProcessingException; // 필요하다면 추가
import com.fasterxml.jackson.databind.ObjectMapper;     // 필요하다면 추가

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor // Lombok이 인자 없는 생성자를 자동으로 생성해줍니다.
public class PublicationRequested extends AbstractEvent {

    private Long manuscriptId; // PolicyHandler에서 event.getId()를 사용하므로 필요합니다.
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorId; // PolicyHandler에서 Long 타입으로 사용하므로 Long으로 정의합니다.
    private String authorName;
}