package aivlecloudnative.domain;

import org.springframework.beans.BeanUtils;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@ToString
@EqualsAndHashCode(callSuper = false) // AbstractEvent를 상속받는 경우
@NoArgsConstructor
public class AutoPublished extends AbstractEvent {

    private Long id;
    private String title;
    private String content;
    private Long manuscriptId;
    private String summary;
    private String keywords;
    private String authorId; // <--- 이 부분을 추가합니다!
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    private String status;

    public AutoPublished(BookWork aggregate) {
        super();
        // BookWork 객체의 모든 필드가 AutoPublished 객체의 동일한 이름의 필드로 복사됩니다.
        // 이제 BookWork에 authorId가 있다면, AutoPublished의 authorId로 자동 복사됩니다.
        BeanUtils.copyProperties(aggregate, this);
    }
}