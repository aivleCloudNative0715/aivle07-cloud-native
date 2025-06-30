// aivlecloudnative.domain.PointDeductionCommand.java
package aivlecloudnative.domain;

// import lombok.Data; // <-- 제거 또는 주석 처리
// import lombok.ToString; // <-- 제거 또는 주석 처리

// @Data // <-- 제거 또는 주석 처리
// @ToString // <-- 제거 또는 주석 처리
public class PointDeductionCommand {
    private Long userId;
    private Long pointsToDeduct; // 차감할 포인트 양

    // --- 수동으로 Getter/Setter 추가 (Lombok 제거 시 필수) ---
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPointsToDeduct() {
        return pointsToDeduct;
    }

    public void setPointsToDeduct(Long pointsToDeduct) {
        this.pointsToDeduct = pointsToDeduct;
    }
    // --- End Getter/Setter ---
}