package aivlecloudnative.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PointQueryResponse {
    private Long id;          // 포인트 고유 ID
    private String userId;    // 사용자 ID
    private Long currentPoints; // 현재 포인트
}