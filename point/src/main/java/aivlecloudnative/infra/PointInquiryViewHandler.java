package aivlecloudnative.infra;

import aivlecloudnative.config.kafka.KafkaProcessor;
import aivlecloudnative.domain.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PointInquiryViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private PointInquiryRepository pointInquiryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPointsGranted_then_CREATE_1(
        @Payload PointsGranted pointsGranted
    ) {
        try {
            if (!pointsGranted.validate()) return;

            // view 객체 생성
            PointInquiry pointInquiry = new PointInquiry();
            // view 객체에 이벤트의 Value 를 set 함
            pointInquiry.setUserId(pointsGranted.getUserId());
            pointInquiry.setCurrentPoints(pointsGranted.getCurrentPoints());
            // view 레파지 토리에 save
            pointInquiryRepository.save(pointInquiry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPointsDeducted_then_UPDATE_1(
        @Payload PointsDeducted pointsDeducted
    ) {
        try {
            if (!pointsDeducted.validate()) return;
            // view 객체 조회

            List<PointInquiry> pointInquiryList = pointInquiryRepository.findByUserId(
                pointsDeducted.getUserId()
            );
            for (PointInquiry pointInquiry : pointInquiryList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                pointInquiry.setCurrentPoints(
                    pointsDeducted.getCurrentPoints()
                );
                // view 레파지 토리에 save
                pointInquiryRepository.save(pointInquiry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
