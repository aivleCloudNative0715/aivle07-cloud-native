package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/points")
@Transactional
public class PointController {

    @Autowired
    PointRepository pointRepository;

    @RequestMapping(
        value = "/points/{id}/pointdeduction",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Point pointDeduction(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /point/pointDeduction  called #####");
        Optional<Point> optionalPoint = pointRepository.findById(id);

        optionalPoint.orElseThrow(() -> new Exception("No Entity Found"));
        Point point = optionalPoint.get();
        point.pointDeduction();

        pointRepository.save(point);
        return point;
    }
}
//>>> Clean Arch / Inbound Adaptor
