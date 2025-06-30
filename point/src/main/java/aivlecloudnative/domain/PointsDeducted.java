package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent; // AbstractEvent를 상속받기 위해 임포트
import java.time.LocalDateTime; // LocalDateTime 사용 시 필요

// PointsDeducted 이벤트 클래스
// AbstractEvent를 상속받아 이벤트 발행 기능을 상속받습니다.
public class PointsDeducted extends AbstractEvent {

    private Long id; // 이벤트 발생 주체의 ID (예: Point 엔티티의 ID)
    private Long userId; // 포인트가 차감된 사용자 ID
    private Long deductedPoints; // 차감된 포인트 양 (Long 타입으로 정의)
    private Integer currentPoints; // 차감 후 현재 남은 총 포인트

    // 기본 생성자 (Jackson 직렬화를 위해 필요)
    public PointsDeducted() {
        super(); // AbstractEvent의 생성자 호출 (timestamp 설정)
    }

    // Point 엔티티를 기반으로 이벤트를 생성하는 생성자
    public PointsDeducted(Point aggregate) {
        super();
        this.id = aggregate.getId();
        this.userId = aggregate.getUserId();
        this.currentPoints = aggregate.getCurrentPoints();
        // deductedPoints는 이벤트 발생 시점에 명시적으로 설정되어야 하므로,
        // 이 생성자에서는 직접 aggregate에서 가져오지 않습니다.
        // setDeductedPoints()를 통해 외부에서 설정되어야 합니다.
    }

    // --- Getters and Setters ---
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

    public Long getDeductedPoints() {
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

    // AbstractEvent의 validate 메서드를 오버라이드하여 이 이벤트의 유효성을 검사할 수 있습니다.
    @Override
    public boolean validate() {
        // 필수 필드가 null이 아닌지, 포인트가 음수가 아닌지 등 검증 로직 추가
        return super.validate() && userId != null && deductedPoints != null;
    }
}