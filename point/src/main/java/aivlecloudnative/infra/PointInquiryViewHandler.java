package aivlecloudnative.infra;

    import aivlecloudnative.domain.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import java.util.Optional;
    import java.util.function.Consumer;
    import java.util.List;
    import aivlecloudnative.domain.TransactionType;

    @Service
    @Transactional
    public class PointInquiryViewHandler {

        @Autowired
        private PointInquiryRepository pointInquiryRepository;

        @Bean
        public Consumer<PointsGranted> processPointsGrantedEvent() { // <-- 메서드 이름 확인
            return pointsGranted -> {
                try {
                    if (!pointsGranted.validate()) {
                        System.err.println("##### PointsGranted event validation failed: " + pointsGranted.toJson() + "\n");
                        return;
                    }

                    System.out.println(
                        "\n\n##### Listener: Received PointsGranted event: " + pointsGranted.toJson() + "\n\n"
                    );

                    PointInquiry pointInquiry = new PointInquiry();
                    pointInquiry.setUserId(pointsGranted.getUserId());
                    pointInquiry.setPoints(pointsGranted.getPointsGrantedAmount());
                    pointInquiry.setTransactionType(TransactionType.GRANTED.name());
                    // pointInquiry.setTransactionDate(pointsGranted.getTimestamp());

                    pointInquiryRepository.save(pointInquiry);
                    System.out.println("##### Read Model Updated: PointInquiry saved for PointsGranted event. User: " + pointsGranted.getUserId() + "\n");

                } catch (Exception e) {
                    System.err.println("##### Error processing PointsGranted event: " + e.getMessage());
                    e.printStackTrace();
                }
            };
        }

        @Bean
        public Consumer<PointsDeducted> processPointsDeductedEvent() { // <-- 메서드 이름 확인
            return pointsDeducted -> {
                try {
                    if (!pointsDeducted.validate()) {
                        System.err.println("##### PointsDeducted event validation failed: " + pointsDeducted.toJson() + "\n");
                        return;
                    }

                    System.out.println(
                        "\n\n##### Listener: Received PointsDeducted event: " + pointsDeducted.toJson() + "\n\n"
                    );

                    PointInquiry pointInquiry = new PointInquiry();
                    pointInquiry.setUserId(pointsDeducted.getUserId());
                    pointInquiry.setPoints(pointsDeducted.getDeductedPoints().intValue());
                    pointInquiry.setTransactionType(TransactionType.DEDUCTED.name());
                    // pointInquiry.setTransactionDate(pointsDeducted.getTimestamp());

                    pointInquiryRepository.save(pointInquiry);
                    System.out.println("##### Read Model Updated: PointInquiry saved for PointsDeducted event. User: " + pointsDeducted.getUserId() + "\n");

                } catch (Exception e) {
                    System.err.println("##### Error processing PointsDeducted event: " + e.getMessage());
                    e.printStackTrace();
                }
            };
        }
    }
    