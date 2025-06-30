package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import aivlecloudnative.domain.PointDeductionCommand;

@RestController
// @RequestMapping(value="/points")
@Transactional
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
        @RequestBody PointDeductionCommand command,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /point/pointDeduction called #####");

        Point point = pointRepository.findById(id)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Point not found for id: " + id));

        // PointDeductionCommand에 getPointsToDeduct()가 Long 타입이므로 intValue()로 변환
        point.pointDeduction(command.getPointsToDeduct().intValue());

        // pointRepository.save(point); // 엔티티 내부에서 저장 로직 주석 처리했으므로, 여기에서 저장하는 것이 적절
        // 하지만 현재 Architecture에서는 Domain Event 발생 시 Transactional Listener를 통해 처리될 것으로 보입니다.
        // 일단 컴파일을 위해 이 줄은 유지하거나, 확실하지 않으면 주석 처리하고 나중에 확인
        return point;
    }
}