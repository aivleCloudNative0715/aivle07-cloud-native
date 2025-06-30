package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean; 
import java.util.function.Consumer; 


@Service
public class PointInquiryViewHandler {

    @Autowired
    private PointInquiryRepository pointInquiryRepository;

    @Bean
    public Consumer<PointsGranted> whenPointsGranted() {
        return pointsGranted -> {
            try {
                if (!pointsGranted.validate()) return;

                System.out.println(
                    "\n\n##### listener PointsGranted : " + pointsGranted.toJson() + "\n\n"
                );

                // PointsGranted 이벤트 발생 시 새로운 PointInquiry 엔티티를 생성하거나 업데이트
                // 여기서는 새로운 엔티티를 생성하는 로직으로 보입니다.
                PointInquiry pointInquiry = new PointInquiry();
                pointInquiry.setUserId(pointsGranted.getUserId());
                pointInquiry.setPoints(pointsGranted.getPointsGrantedAmount()); // PointsGranted에 getPointsGrantedAmount()가 있다고 가정
                pointInquiry.setTransactionType("GRANTED");

                pointInquiryRepository.save(pointInquiry);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    public Consumer<PointsDeducted> whenPointsDeducted() {
        return pointsDeducted -> {
            try {
                if (!pointsDeducted.validate()) return;

                System.out.println(
                    "\n\n##### listener PointsDeducted : " + pointsDeducted.toJson() + "\n\n"
                );

                // findByUserId는 List를 반환하므로, List로 받고 첫 번째 항목을 Optional로 변환
                List<PointInquiry> pointInquiryList = pointInquiryRepository.findByUserId(pointsDeducted.getUserId());
                Optional<PointInquiry> optionalPointInquiry = pointInquiryList.stream().findFirst();

                optionalPointInquiry.ifPresent(pointInquiry -> {
                    // PointsDeducted 클래스에서 getDeductedPoints()로 필드명을 변경했으므로, 해당 Getter를 사용합니다.
                    pointInquiry.setPoints(pointInquiry.getPoints() - pointsDeducted.getDeductedPoints().intValue()); // Long -> Integer 변환
                    pointInquiry.setTransactionType("DEDUCTED");

                    pointInquiryRepository.save(pointInquiry);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}