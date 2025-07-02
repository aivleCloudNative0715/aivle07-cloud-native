package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDateTime; // LocalDateTime 임포트 추가
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AuthorApplied extends AbstractEvent {

    private Long id; // Author의 PK (DB에서 자동 생성된 ID)
    private Long userId; // JWT에서 추출한 User의 고유 ID
    private String authorId; // 이메일 형식 (User 서버의 email과 동일)
    private String authorName; // 작가 이름
    private String bio; // 작가 소개
    private String representativeWork; // 대표작
    private String portfolio; // 포트폴리오 URL 또는 내용
    private LocalDateTime appliedAt; // 작가 신청 시간

    public AuthorApplied(Author aggregate) {
        super(aggregate); // AbstractEvent의 생성자를 호출하여 aggregate의 속성을 복사
        // 복사되지 않는 필드는 여기서 명시적으로 설정
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.authorId = aggregate.getAuthorId();
        this.authorName = aggregate.getAuthorName();
        this.bio = aggregate.getBio();
        this.representativeWork = aggregate.getRepresentativeWork();
        this.portfolio = aggregate.getPortfolio();
        this.appliedAt = aggregate.getAppliedAt();
    }

    public AuthorApplied() {
        super();
    }
}
//>>> DDD / Domain Event