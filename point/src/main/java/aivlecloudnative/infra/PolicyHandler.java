package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Bean; 
import java.util.function.Consumer; 


@Service
@Transactional // PolicyHandler가 서비스 계층이고 DB 작업을 하므로 @Transactional 유지
public class PolicyHandler {

    @Autowired
    PointRepository pointRepository;

    // Spring Cloud Stream 3.x의 함수형 모델로 변경
    @Bean 
    public Consumer<SubscriberSignedUp> wheneverSubscriberSignedUp() { 
        return subscriberSignedUp -> { 
            try {
                if (!subscriberSignedUp.validate()) return;

                System.out.println(
                    "\n\n##### listener wheneverSubscriberSignedUp : " + subscriberSignedUp.toJson() + "\n\n"
                );

                // Find or create Point entity for the user
                // findByUserId 메서드가 PointRepository에 정의되어 있고 Optional<Point>를 반환한다고 가정
                Point point = pointRepository.findByUserId(subscriberSignedUp.getId()).orElseGet(() -> {
                    Point newPoint = new Point();
                    newPoint.setUserId(subscriberSignedUp.getId());
                    newPoint.setIsKTmember(true);
                    newPoint.setCurrentPoints(0);
                    return newPoint;
                });

                // Business Logic: Grant 1000 points upon sign-up
                point.setCurrentPoints(point.getCurrentPoints() + 1000);

                // save 메서드는 JpaRepository에 의해 제공됩니다. PointRepository가 JpaRepository를 상속받았다면 오류가 사라집니다.
                pointRepository.save(point);

                // Publish PointsGranted event
                PointsGranted pointsGranted = new PointsGranted(point);
                pointsGranted.setUserId(point.getUserId());
                pointsGranted.setPointsGrantedAmount(point.getCurrentPoints()); // PointsGranted의 setPointsGrantedAmount도 확인 필요 (PointsGranted.java도 lombok 제거 시 수동 생성)
                pointsGranted.publishAfterCommit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}