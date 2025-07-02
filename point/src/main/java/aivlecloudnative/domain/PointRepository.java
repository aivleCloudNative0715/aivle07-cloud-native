package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, String> {
    Optional<Point> findByUserId(String userId);
}