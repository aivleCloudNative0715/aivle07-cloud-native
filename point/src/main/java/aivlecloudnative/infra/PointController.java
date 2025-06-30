package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // ResponseStatusException 임포트
import org.springframework.http.HttpStatus; // HttpStatus 임포트
import org.springframework.transaction.annotation.Transactional; // Spring의 @Transactional 임포트

// 추가: PointDeductionCommand 임포트 (이미 있었지만 다시 확인)
import aivlecloudnative.domain.PointDeductionCommand; 

@RestController
// @RequestMapping(value="/points") // 이 어노테이션은 현재 없으므로 주석 처리된 상태 유지
@Transactional // Spring의 Transactional 사용
public class PointController {

    @Autowired
    PointRepository pointRepository;

    @RequestMapping(
        value = "/points/{id}/pointdeduction",
        method = RequestMethod.PUT,
        consumes = "application/json;charset=UTF-8",
        produces = "application/json;charset=UTF-8"
    )
    public Point pointDeduction(
        @PathVariable(value = "id") Long id,
        @RequestBody PointDeductionCommand command, // PointDeductionCommand를 @RequestBody로 받음
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /point/pointDeduction called #####");
        
        // findById는 Optional을 반환하므로 .orElseThrow()를 사용하여 Point 객체를 가져옵니다.
        // HttpStatus.NOT_FOUND를 사용하여 404 응답을 보낼 수 있습니다.
        Point point = pointRepository.findById(id)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point not found for id: " + id));

        // point.pointDeduction() 메서드는 Integer deductionAmount를 인자로 받으므로,
        // command 객체에서 해당 금액을 추출하여 전달해야 합니다.
        // PointDeductionCommand 클래스에 getAmount() 또는 getDeductionAmount()와 같은 메서드가 있다고 가정합니다.
        // 실제 PointDeductionCommand 클래스에 정의된 필드에 맞는 getter를 사용해야 합니다.
        // 예를 들어 PointDeductionCommand에 private Integer amount; 필드가 있다면 command.getAmount() 사용
        // 또는 private Integer deductionAmount; 필드가 있다면 command.getDeductionAmount() 사용
        point.pointDeduction(command.getAmount()); // <-- PointDeductionCommand에 getAmount()가 있다고 가정

        // pointRepository.save(point); // Point 클래스 내부에서 publishAfterCommit()이 호출되면서 저장될 것으로 예상되므로, 일반적으로 컨트롤러에서 save를 직접 호출하지 않습니다.
                                         // 만약 도메인 이벤트 발행과 별도로 직접 저장이 필요하면 남겨두세요.
        return point;
    }
}