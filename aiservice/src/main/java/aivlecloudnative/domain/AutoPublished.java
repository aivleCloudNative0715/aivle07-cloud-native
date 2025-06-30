package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import aivlecloudnative.infra.AbstractEvent; // 올바른 AbstractEvent import

@Data
@EqualsAndHashCode(callSuper = false) // AbstractEvent를 상속받는 경우
public class AutoPublished extends AbstractEvent {

    private Long id; // BookWork의 ID
    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status;
    private String authorName;

    public AutoPublished() {
        super(); // AbstractEvent의 기본 생성자 호출
    }
}