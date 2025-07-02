package aivlecloudnative.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    @Id
    private String id; // 포인트 고유 ID (UUID)
    private String userId;
    private Long currentPoints;
    private Boolean isKTmember;

    // 초기 포인트 지급을 위한 팩토리 메서드
    public static Point createInitialPoint(String userId, boolean isKTmember) {
        return Point.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .currentPoints(isKTmember ? 5000L : 1000L) // KT 고객 5000, 일반 1000
                .isKTmember(isKTmember)
                .build();
    }

    // 포인트 업데이트 메서드 (여기서는 초기 지급만 다루므로 단순화)
    public void addPoints(Long points) {
        this.currentPoints += points;
    }
}