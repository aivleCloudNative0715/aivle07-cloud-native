package aivlecloudnative.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime; // LocalDateTime 임포트

@Entity
@Table(name = "author")
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private Long userId;

    @Column(unique = true)
    private String authorId; // 이메일 형식 (User 서버의 email과 동일)

    private String authorName;
    private String bio;
    private String representativeWork;
    private String portfolio;

    // --- 작가 신청/승인/거부 상태를 나타내는 시간 필드들 ---
    private LocalDateTime appliedAt; // 작가 신청 시간 (필수)
    private LocalDateTime acceptedAt; // 작가 승인 시간 (승인 시 설정, 그 외 null)
    private LocalDateTime rejectedAt; // 작가 거부 시간 (거부 시 설정, 그 외 null)
    // ---

    // 기본 생성자
    public Author() {}

    // 작가 신청 시 사용될 생성자 (최초 신청 또는 재신청 시)
    public Author(Long userId, String authorId, String authorName, String bio, String representativeWork, String portfolio) {
        this.userId = userId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.bio = bio;
        this.representativeWork = representativeWork;
        this.portfolio = portfolio;
        this.appliedAt = LocalDateTime.now(); // 현재 시간으로 신청 시간 설정
        this.acceptedAt = null; // 초기에는 승인/거부 시간 없음
        this.rejectedAt = null;
    }

    // --- 도메인 로직 메서드 ---

    // 작가 신청 또는 재신청 로직
    public void applyForAuthor(String authorName, String bio, String representativeWork, String portfolio) {
        // 필드 업데이트 (재신청 시 새로운 정보로 갱신 가능)
        this.authorName = authorName;
        this.bio = bio;
        this.representativeWork = representativeWork;
        this.portfolio = portfolio;

        // 재신청 시: 신청 시간 갱신, 승인/거부 시간 초기화
        this.appliedAt = LocalDateTime.now();
        this.acceptedAt = null;
        this.rejectedAt = null;
    }

    // 작가 승인 로직
    public void acceptAuthor() {
        this.acceptedAt = LocalDateTime.now(); // 현재 시간으로 승인 시간 설정
        this.rejectedAt = null; // 거부 시간은 null로 초기화 (승인되었으므로)
    }

    // 작가 거부 로직
    public void rejectAuthor() {
        this.rejectedAt = LocalDateTime.now(); // 현재 시간으로 거부 시간 설정
        this.acceptedAt = null; // 승인 시간은 null로 초기화 (거부되었으므로)
    }

    // --- 현재 작가의 상태를 파악하는 헬퍼 메서드 (선택 사항) ---
    public String getStatus() {
        if (acceptedAt != null) {
            return "ACCEPTED";
        } else if (rejectedAt != null) {
            return "REJECTED";
        } else if (appliedAt != null) {
            return "APPLIED"; // appliedAt이 있고 acceptedAt, rejectedAt이 null인 경우
        }
        return "UNKNOWN"; // 모든 필드가 null인 경우는 없어야 함 (appliedAt은 필수)
    }
}