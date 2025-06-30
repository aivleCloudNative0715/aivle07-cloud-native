package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
// import lombok.*; // <-- 제거 또는 주석 처리
// import lombok.EqualsAndHashCode; // <-- 제거 또는 주석 처리

//<<< DDD / Domain Event
// @Data // <-- 제거 또는 주석 처리
// @ToString // <-- 제거 또는 주석 처리
// @EqualsAndHashCode(callSuper = false) // <-- 제거 또는 주석 처리
public class PointsDeducted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long deductedPoints; // 필드명은 deductedPoints
    private Integer currentPoints;

    // --- 수동으로 생성자 추가 ---
    public PointsDeducted(Point aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.currentPoints = aggregate.getCurrentPoints();
        // aggregate에서 실제 차감된 포인트를 가져와야 합니다.
        // Point 엔티티에 차감된 포인트를 저장하는 필드나 메서드가 필요할 수 있습니다.
        // 현재 로직상 aggregate.getCurrentPoints()는 현재 남은 포인트이므로,
        // 차감된 포인트를 정확히 얻으려면 PointDeducted 이벤트 발생 시점의 로직을 수정해야 합니다.
        // 일단 예시 값은 그대로 두지만, 실제 구현에서는 이 부분을 조정해야 합니다.
        this.deductedPoints = 500L; // 이 값은 예시입니다. 실제 차감된 포인트를 반영하도록 로직 변경 필요.
    }

    public PointsDeducted() {
        super();
    }
    // --- End 생성자 ---


    // --- 수동으로 Getter/Setter 추가 ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 이전에 오류났던 getPointsDeductedAmount() 대신, 실제 필드인 deductedPoints의 Getter를 추가합니다.
    public Long getDeductedPoints() { // <-- 수정: 필드명에 맞게 getDeductedPoints
        return deductedPoints;
    }

    public void setDeductedPoints(Long deductedPoints) {
        this.deductedPoints = deductedPoints;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }
    // --- End Getter/Setter ---
}