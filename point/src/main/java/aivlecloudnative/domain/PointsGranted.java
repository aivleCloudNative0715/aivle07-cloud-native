package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
// lombok 관련 임포트는 모두 제거하거나 주석 처리합니다.

// @Data // <-- 이 어노테이션 제거 또는 주석 처리
// @EqualsAndHashCode(callSuper = false) // <-- 이 어노테이션 제거 또는 주석 처리
public class PointsGranted extends AbstractEvent {

    private Long id; // 이벤트 ID (필요하다면)
    private Long userId; // 사용자의 ID
    private Integer pointsGrantedAmount; // <-- 이 필드가 있는지 반드시 확인!

    // --- 수동으로 생성자 추가 ---
    // 기본 생성자 (이벤트 역직렬화 시 필요)
    public PointsGranted() {
        super();
    }

    // Point 엔티티를 받아 이벤트를 생성하는 생성자
    public PointsGranted(Point aggregate) {
        super(aggregate); // AbstractEvent의 생성자 호출
        this.userId = aggregate.getUserId();
        this.pointsGrantedAmount = aggregate.getCurrentPoints(); // Point의 currentPoints 값을 가져옴
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

    public Integer getPointsGrantedAmount() {
        return pointsGrantedAmount;
    }

    public void setPointsGrantedAmount(Integer pointsGrantedAmount) {
        this.pointsGrantedAmount = pointsGrantedAmount;
    }
    // --- End Getter/Setter ---
}