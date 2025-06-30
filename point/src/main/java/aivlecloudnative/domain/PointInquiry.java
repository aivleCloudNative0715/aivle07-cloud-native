package aivlecloudnative.domain;

import jakarta.persistence.*; // 또는 jakarta.persistence.*
// lombok 관련 임포트는 모두 제거하거나 주석 처리합니다.

@Entity
@Table(name = "PointInquiry_table")
// @Data // <-- 이 어노테이션 제거 또는 주석 처리
// @NoArgsConstructor // <-- 이 어노테이션 제거 또는 주석 처리
// @AllArgsConstructor // <-- 이 어노테이션 제거 또는 주석 처리
public class PointInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // 또는 GenerationType.IDENTITY
    private Long id;
    private Long userId;
    private Integer points;
    private String transactionType; // GRANTED, DEDUCTED 등

    // --- 수동으로 생성자 추가 ---
    // 기본 생성자 (JPA 엔티티에 필수)
    public PointInquiry() {}

    // 모든 필드를 포함하는 생성자 (선택 사항, 필요하다면 추가)
    public PointInquiry(Long id, Long userId, Integer points, String transactionType) {
        this.id = id;
        this.userId = userId;
        this.points = points;
        this.transactionType = transactionType;
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    // --- End Getter/Setter ---
}