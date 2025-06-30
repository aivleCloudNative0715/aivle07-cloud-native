package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor; // ✨ BeanUtils.copyProperties를 위해 기본 생성자가 필요합니다.
import aivlecloudnative.infra.AbstractEvent; // 올바른 AbstractEvent import

@Data
@EqualsAndHashCode(callSuper = false) // AbstractEvent를 상속받는 경우
@NoArgsConstructor // ✨ @NoArgsConstructor 추가
public class AutoPublished extends AbstractEvent {

    private Long id; // BookWork의 ID를 그대로 사용
    private Long manuscriptIdId; // ✨ BookWork와 동일하게 'manuscriptIdId'로 변경
    private String title;
    private String content; // ✨ BookWork와 동일하게 'content' 필드 추가
    private String summary; // BookWork의 최종 summary를 반영
    private String keywords;
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    private String status; // BookWork의 상태 (예: "AutoPublished")

    // 기본 생성자는 @NoArgsConstructor로 자동 생성됩니다.
    // 만약 특정 인자를 받는 생성자가 필요하다면 직접 정의할 수 있습니다.
    // (BeanUtils.copyProperties 사용 시 필수 아님)
    public AutoPublished(
            Long id,
            Long manuscriptIdId,
            String title,
            String content,
            String summary,
            String keywords,
            String authorName,
            String coverImageUrl,
            String ebookUrl,
            String category,
            Integer price,
            String status) {
        super();
        this.id = id;
        this.manuscriptIdId = manuscriptIdId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.keywords = keywords;
        this.authorName = authorName;
        this.coverImageUrl = coverImageUrl;
        this.ebookUrl = ebookUrl;
        this.category = category;
        this.price = price;
        this.status = status;
    }
}