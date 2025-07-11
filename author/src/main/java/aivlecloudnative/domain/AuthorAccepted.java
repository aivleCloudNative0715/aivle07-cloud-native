package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorAccepted extends AbstractEvent {

    private Long id; // 승인된 Author의 PK
    private Long userId; // 승인된 User의 고유 ID

    public AuthorAccepted(Author aggregate) {
        super(aggregate); // AbstractEvent의 생성자를 호출하여 aggregate의 속성을 복사
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId(); // userId 필드 추가 및 설정
    }

    public AuthorAccepted() {
        super();
    }
}
//>>> DDD / Domain Event