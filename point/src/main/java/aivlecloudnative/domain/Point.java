package aivlecloudnative.domain;

import jakarta.persistence.*;
import org.springframework.context.ApplicationContext; 
// lombok 관련 임포트는 모두 제거합니다. (lombok.Data, lombok.NoArgsConstructor 등)

@Entity
@Table(name = "Point_table")
// @Data // <-- 이 어노테이션을 제거
// @NoArgsConstructor // <-- 이 어노테이션을 제거
// @AllArgsConstructor // <-- 이 어노테이션을 제거
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // 또는 GenerationType.IDENTITY
    private Long id;
    private Long userId;
    private Integer currentPoints;
    private Boolean isKTmember; // KT 멤버십 여부

    // --- 수동으로 생성자 추가 ---
    // 기본 생성자 (JPA 엔티티에 필수)
    public Point() {}

    // 모든 필드를 포함하는 생성자 (선택 사항, 필요하다면 추가)
    public Point(Long id, Long userId, Integer currentPoints, Boolean isKTmember) {
        this.id = id;
        this.userId = userId;
        this.currentPoints = currentPoints;
        this.isKTmember = isKTmember;
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

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Boolean getIsKTmember() {
        return isKTmember;
    }

    public void setIsKTmember(Boolean KTmember) { // 파라미터 이름은 소문자로 시작하는 것이 관례
        isKTmember = KTmember;
    }
    // --- End Getter/Setter ---


    @PostPersist
    public void onPostPersist() {
        // PointsGranted 이벤트를 발행하는 로직 (이 부분은 유지)
        PointsGranted pointsGranted = new PointsGranted(this);
        pointsGranted.publishAfterCommit();
    }

    @PostUpdate
    @PostRemove
    public void onPostUpdate() {
        // 이벤트 발행 로직 (예: PointsUpdated, PointsDeducted 등)
        // 이 부분은 현재 오류와 직접 관련 없으므로 유지
    }

    // Spring Data JPA Repository를 직접 접근하는 정적 메서드
    private static ApplicationContext applicationContext; // static 필드 선언 (이미 있지만 명시)

    public static PointRepository repository() {
        PointRepository pointRepository = applicationContext.getBean(PointRepository.class);
        return pointRepository;
    }

    // pointDeduction 메서드 (수정: command 객체에서 금액을 추출하도록)
    // 현재 PointController에서 PointDeductionCommand 객체 전체를 넘겨주고 있으므로
    // 이 메서드 시그니처가 'Integer deductionAmount'로 되어 있다면 충돌합니다.
    // 두 가지 해결책:
    // 1. PointController에서 command.getAmount() 등을 넘기도록 수정
    // 2. PointDeductionCommand 객체를 받도록 메서드 시그니처 변경 (아래는 1번 방식을 택합니다.)
    public void pointDeduction(Integer deductionAmount) { // 이 부분은 그대로 둡니다.
        if (this.currentPoints >= deductionAmount) {
            this.currentPoints -= deductionAmount;
            // repository().save(this); // <-- 이 라인 주석 처리 (엔티티 내부에서 저장 금지)
            PointsDeducted pointsDeducted = new PointsDeducted(this);
            pointsDeducted.publishAfterCommit();
        } else {
            System.out.println("잔액 부족");
        }
    }

    // pointPayment 메서드 (수정: save 호출 주석 처리)
    public void pointPayment(Integer paymentAmount) {
        this.currentPoints += paymentAmount;
        // repository().save(this); // <-- 이 라인 주석 처리 (엔티티 내부에서 저장 금지)
        // PointsPaymented pointsPaymented = new PointsPaymented(this);
        // pointsPaymented.publishAfterCommit();
    }

    // applicationContext 주입 (유지)
    @Transient
    // private static ApplicationContext applicationContext; // 이미 위에서 선언됨

    public static void setApplicationContext(ApplicationContext applicationContext) {
        Point.applicationContext = applicationContext;
    }
}