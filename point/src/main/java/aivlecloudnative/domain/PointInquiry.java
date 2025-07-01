package aivlecloudnative.domain;

import jakarta.persistence.*; // JPA 관련 임포트

@Entity
@Table(name = "PointInquiryTable") // 테이블 이름을 명확히 지정하는 것이 좋습니다.
public class PointInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ID 자동 생성 전략
    private Long id;

    private Long userId;

    private Integer points;

    // TransactionType 필드를 String으로 변경하고 @Enumerated(EnumType.STRING) 추가
    // @Enumerated(EnumType.STRING) // Enum의 이름을 문자열로 DB에 저장하도록 지정
    private String transactionType; // <-- 타입을 TransactionType enum에서 String으로 변경!

    // private LocalDateTime transactionDate; // 필요시 주석 해제

    public PointInquiry() {
        // 기본 생성자
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    // setTransactionType 메서드의 매개변수도 String으로 변경합니다.
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) { // <-- 매개변수 타입을 String으로 변경
        this.transactionType = transactionType;
    }

    // public LocalDateTime getTransactionDate() {
    //     return transactionDate;
    // }
    //
    // public void setTransactionDate(LocalDateTime transactionDate) {
    //     this.transactionDate = transactionDate;
    // }
}