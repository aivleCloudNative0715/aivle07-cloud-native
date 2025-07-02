package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccessRequestedWithPoints extends AbstractEvent {
    private String userId;
    private String bookId; // 열람을 요청한 도서의 ID
}