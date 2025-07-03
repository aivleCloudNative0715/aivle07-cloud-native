// src/main/java/aivlecloudnative/infra/PointController.java
package aivlecloudnative.infra;

import aivlecloudnative.domain.PointRepository;
import aivlecloudnative.domain.PointQueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/points") // 포인트 관련 API의 기본 경로
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointRepository pointRepository;

    public PointController(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    // userId로 유저의 포인트 조회하는 API
    @GetMapping("/{userId}")
    public ResponseEntity<PointQueryResponse> getPointByUserId(@PathVariable String userId) {
        log.info("Attempting to retrieve points for userId: {}", userId);

        return pointRepository.findByUserId(Long.valueOf(userId))
                .map(point -> {
                    PointQueryResponse response = new PointQueryResponse(
                        point.getId(),
                        point.getUserId(),
                        point.getCurrentPoints()
                    );
                    log.info("Successfully retrieved points for userId {}: {}", userId, response);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Points not found for userId: {}", userId);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }
}