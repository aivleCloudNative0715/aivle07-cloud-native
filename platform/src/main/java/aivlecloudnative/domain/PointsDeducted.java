package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PointsDeducted extends AbstractEvent {

    private Long userId;
    private Long bookId; // 어떤 책 열람에 대한 포인트 차감인지

    public PointsDeducted(Long userId, Long bookId) {
        super();
        this.userId = userId;
        this.bookId = bookId;
    }
}