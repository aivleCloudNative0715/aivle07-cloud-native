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
    private String authorName;

    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price;

    private String status;

    public AutoPublished(BookWork aggregate) {
        super();
        BeanUtils.copyProperties(aggregate, this); // BookWork의 필드를 AutoPublished로 복사
    }
}