package aivlecloudnative.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import aivlecloudnative.infra.AbstractEvent; // 올바른 AbstractEvent import

@Data
@EqualsAndHashCode(callSuper = false) // AbstractEvent를 상속받는 경우
public class PublicationInfoCreationRequested extends AbstractEvent {

    private Long id; // BookWork의 ID
    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String authorName; // BookWork에서 복사되는 필드이므로 추가하는 것이 맞습니다.
    // AI 처리에 필요한 다른 정보들도 여기에 포함될 수 있습니다.

    public PublicationInfoCreationRequested() {
        super(); // AbstractEvent의 기본 생성자 호출
    }
}