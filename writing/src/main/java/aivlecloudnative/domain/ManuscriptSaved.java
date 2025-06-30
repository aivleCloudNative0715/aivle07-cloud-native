package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent; // infra 패키지 임포트 유지
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor; // Lombok의 기본 생성자 추가 (안전성을 위해)

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor // Added for safety and deserialization
public class ManuscriptSaved extends AbstractEvent {

    private Long manuscriptId;
    private String authorId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String status;
    private LocalDateTime lastModifiedAt;

    public ManuscriptSaved(Manuscript aggregate) {
        super();
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.content = aggregate.getContent();
        this.summary = aggregate.getSummary();
        this.keywords = aggregate.getKeywords();
        this.status = aggregate.getStatus();
        this.lastModifiedAt = aggregate.getLastModifiedAt();
    }
    @Override
    public String getEventType() {
        return "ManuscriptSaved";
    }
}