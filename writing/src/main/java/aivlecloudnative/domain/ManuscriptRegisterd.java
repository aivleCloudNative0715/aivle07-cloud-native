package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

//<<< DDD / Domain Event

@Data
@EqualsAndHashCode(callSuper = false)
public class ManuscriptRegisterd extends AbstractEvent {

    private Long manuscriptId; // 또는 id로 유지해도 무방
    private String authorId;
    private String title;
    private String status; // 초기 상태 (REGISTERED)

    public ManuscriptRegisterd(Manuscript aggregate) {
        super();
        this.manuscriptId = aggregate.getId();
        this.authorId = aggregate.getAuthorId();
        this.title = aggregate.getTitle();
        this.status = aggregate.getStatus();
        // content, summary, keywords는 제거
    }

    public ManuscriptRegisterd() {
        super();
    }
}
//>>> DDD / Domain Event
