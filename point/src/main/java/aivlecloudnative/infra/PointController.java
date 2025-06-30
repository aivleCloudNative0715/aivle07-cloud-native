package aivlecloudnative.infra; // <-- 이 패키지 선언이 정확해야 합니다.

import aivlecloudnative.domain.Point;
import aivlecloudnative.domain.PointRepository;
import aivlecloudnative.domain.PointsDeducted; // PointsDeducted 이벤트 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // @Transactional 임포트 추가
import java.util.Optional;

@RestController
@RequestMapping("/points")
public class PointController {

    @Autowired
    PointRepository pointRepository; // Point 엔티티의 CRUD를 담당하는 리포지토리 자동 주입

    /**
     * 포인트 차감 API
     * PUT /points/{pointId}/pointdeduction
     * @param pointId 차감할 포인트 엔티티의 ID
     * @param pointsToDeduct 차감할 포인트 양
     * @return 업데이트된 Point 엔티티 또는 오류 응답
     */
    @PutMapping("/{pointId}/pointdeduction")
    @Transactional // 트랜잭션 관리
    public ResponseEntity<?> pointDeduction(
        @PathVariable Long pointId,
        @RequestParam Integer pointsToDeduct // 요청 파라미터로 차감할 포인트 양 받기
    ) {
        System.out.println("##### /point/pointDeduction called #####");

        try {
            System.out.println("##### Debug: Inside try block. Starting point entity lookup.");
            // 1. Point 엔티티 조회
            Optional<Point> optionalPoint = pointRepository.findById(pointId);
            if (!optionalPoint.isPresent()) {
                System.err.println("##### Error: Point with ID " + pointId + " not found.");
                return new ResponseEntity<>("Point not found", HttpStatus.NOT_FOUND);
            }
            Point point = optionalPoint.get();
            System.out.println("##### Debug: Point entity found. Current points: " + point.getCurrentPoints());

            // 2. 포인트 차감 로직
            if (point.getCurrentPoints() < pointsToDeduct) {
                System.err.println("##### Error: Insufficient points for deduction. Current: " + point.getCurrentPoints() + ", Attempted deduction: " + pointsToDeduct);
                return new ResponseEntity<>("Insufficient points", HttpStatus.BAD_REQUEST);
            }
            System.out.println("##### Debug: Points sufficient. Applying deduction.");

            point.setCurrentPoints(point.getCurrentPoints() - pointsToDeduct);
            pointRepository.save(point); // 변경된 포인트 저장
            System.out.println("##### Point Deduction successful. User ID: " + point.getUserId() + ", New points: " + point.getCurrentPoints() + "\n");
            System.out.println("##### Debug: Point entity saved. Preparing event.");

            // 3. PointsDeducted 이벤트 발행
            PointsDeducted pointsDeducted = new PointsDeducted(point);
            pointsDeducted.setUserId(point.getUserId());
            pointsDeducted.setDeductedPoints(Long.valueOf(pointsToDeduct));
            pointsDeducted.setCurrentPoints(point.getCurrentPoints());
            System.out.println("##### Debug: PointsDeducted event created. Publishing event.");
            pointsDeducted.publishAfterCommit(); // 트랜잭션 커밋 후 이벤트 발행

            System.out.println("##### Debug: publishAfterCommit called. Returning response.");
            return new ResponseEntity<>(point, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("##### Error during point deduction (Caught in Controller): " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스도 함께 출력
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 다른 API 엔드포인트가 있다면 여기에 추가
    // 예: 포인트 조회, 포인트 지급 등
}