package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> { // String에서 Long으로 변경
    Optional<Point> findByUserId(String userId);
}