package aivlecloudnative.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime; // LocalDateTime 사용을 위해 임포트

@Entity
@Table(name = "PointInquiryTable") // 테이블 이름을 명확히 지정
public class PointInquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ID 자동 생성 전략
    private Long id;

    private Long userId; // 거래가 발생한 사용자 ID

    private Integer points; // 거래된 포인트 양 (지급 또는 차감)

    private String transactionType; // 거래 타입 (GRANTED, DEDUCTED 등)

    private Long bookId; // <-- 추가: 어떤 책과 관련된 거래인지 기록 (책 열람 시 사용)

    private LocalDateTime transactionDate; // <-- 추가: 거래 발생 시간

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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getTransactionDate() { 
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) { 
        this.transactionDate = transactionDate;
    }
}
