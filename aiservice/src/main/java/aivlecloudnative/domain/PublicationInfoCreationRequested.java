package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import aivlecloudnative.infra.AbstractEvent; // 올바른 AbstractEvent import
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@EqualsAndHashCode(callSuper = false) // AbstractEvent를 상속받는 경우
@NoArgsConstructor // Lombok이 기본 생성자를 자동으로 만들어줍니다.
public class PublicationInfoCreationRequested extends AbstractEvent {

    private Long id; // BookWork의 ID를 그대로 사용합니다.
    private Long manuscriptId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorName;

    // 이 필드들은 AI 처리 과정에서 '생성될' 정보이므로,
    // 이 이벤트가 발행될 때는 초기값이 null이거나 기본값으로 유지됩니다.
    // 이벤트가 AI 서비스로 전달되어 처리된 후, 이 정보들이 채워질 것입니다.
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    // ✨ BookWork 객체를 인자로 받는 생성자를 추가합니다.
    // 이벤트 발행 시 BookWorkController에서 이 생성자를 호출할 것입니다.
    public PublicationInfoCreationRequested(BookWork bookWork) {
        super(); // AbstractEvent의 생성자 호출 (이벤트 생성 시간 등을 설정)
        this.id = bookWork.getId(); // BookWork의 ID를 이벤트의 ID로 사용
        this.manuscriptId = bookWork.getManuscriptId();
        this.title = bookWork.getTitle();
        this.content = bookWork.getContent();
        this.summary = bookWork.getSummary();
        this.keywords = bookWork.getKeywords();
        this.authorName = bookWork.getAuthorName();
        // coverImageUrl, ebookUrl, category, price는 이 시점에서는 null로 유지됩니다.
    }

    @Override // AbstractEvent에 toJson()이 있으므로 override 지시자 추가
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting event to JSON", e);
        }
    }
}