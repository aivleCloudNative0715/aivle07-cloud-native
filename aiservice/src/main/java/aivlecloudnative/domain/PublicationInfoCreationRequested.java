package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import aivlecloudnative.infra.AbstractEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublicationInfoCreationRequested extends AbstractEvent {

    private Long id;
    private Long manuscriptId;
    private String title;
    private String content; // <-- 이 부분을 다시 추가합니다!
    private String summary;
    private String keywords;
    private String authorId;
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    public PublicationInfoCreationRequested(BookWork bookWork) {
        super();
        this.id = bookWork.getId();
        this.manuscriptId = bookWork.getManuscriptId();
        this.title = bookWork.getTitle();
        this.content = bookWork.getContent(); // <-- BookWork에서 content를 가져오도록 추가!
        this.summary = bookWork.getSummary();
        this.keywords = bookWork.getKeywords();
        this.authorId = bookWork.getAuthorId();
        this.authorName = bookWork.getAuthorName();
        // coverImageUrl, ebookUrl, category, price는 이 시점에서는 null로 유지됩니다.
    }

    @Override
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting event to JSON", e);
        }
    }
}