package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import aivlecloudnative.infra.AbstractEvent; // 이 경로가 맞는지 확인해주세요.
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PublicationInfoCreationRequested extends AbstractEvent {

    // ##### 이 부분을 추가했습니다! #####
    private Long bookWorkId; // PolicyHandler에서 setBookWorkId, getBookWorkId 호출에 필요

    private Long id; // 기존 id 필드 (이벤트 자체의 ID 또는 Manuscript ID일 가능성)
    private Long manuscriptId; // 원고 ID로 사용될 가능성
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorId; // authorId는 Long으로 변경 (이전 오류 분석에 따라)
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    // BookWork 엔티티로부터 이벤트를 생성하는 생성자
    public PublicationInfoCreationRequested(BookWork bookWork) {
        super();
        this.setEventType(this.getClass().getSimpleName()); // AbstractEvent의 setEventType 호출
        this.bookWorkId = bookWork.getId(); // BookWork의 ID를 bookWorkId로 설정
        this.id = bookWork.getId(); // 이벤트 자체의 ID로도 BookWork의 ID를 사용할 수 있습니다.
                                   // 또는 이벤트에 고유한 ID가 있다면 그 ID를 사용해야 합니다.
                                   // 현재 PolicyHandler에서는 event.getBookWorkId()만 사용하므로 이 id는 중복일 수 있습니다.
                                   // 만약 event.getId()가 PublicationRequested의 ID를 의미한다면,
                                   // 이 생성자에서는 bookWork.getManuscriptId()를 사용하는 것이 더 적절할 수 있습니다.
                                   // (PolicyHandler의 PublicationInfoCreationRequested 이벤트 발행 부분 참고)

        this.manuscriptId = bookWork.getManuscriptId(); // ManuscriptId는 별도로 설정
        this.title = bookWork.getTitle();
        this.content = bookWork.getContent();
        this.summary = bookWork.getSummary();
        this.keywords = bookWork.getKeywords();
        this.authorId = bookWork.getAuthorId();
        this.authorName = bookWork.getAuthorName();
        // coverImageUrl, ebookUrl, category, price는 이 시점에서는 AI 처리가 완료되지 않았으므로 null로 유지됩니다.
    }

    @Override
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // 더 구체적인 RuntimeException을 던지거나, 로깅 후 빈 JSON 반환 등을 고려할 수 있습니다.
            throw new RuntimeException("Error converting event to JSON", e);
        }
    }
}