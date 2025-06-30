package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Bean; 
import java.util.function.Consumer; 
import java.util.Optional; 

@Service
@Transactional 
public class PolicyHandler {

    @Autowired
    PointRepository pointRepository; 

    @Bean 
    public Consumer<SubscriberSignedUp> wheneverSubscriberSignedUp() { 
        return subscriberSignedUp -> { 
            try {
                if (!subscriberSignedUp.validate()) {
                    System.err.println("##### SubscriberSignedUp event validation failed: " + subscriberSignedUp.toJson() + "\n");
                    return; 
                }

                System.out.println(
                    "\n\n##### Listener: Received SubscriberSignedUp event: " + subscriberSignedUp.toJson() + "\n\n"
                );

                Point point = pointRepository.findByUserId(subscriberSignedUp.getId()).orElseGet(() -> {
                    Point newPoint = new Point();
                    newPoint.setUserId(subscriberSignedUp.getId());
                    // SubscriberSignedUp 이벤트에 isKtMember 필드가 있다고 가정하고 가져옵니다.
                    // 만약 SubscriberSignedUp에 이 필드가 없다면, 사용자 서비스 팀과 협의하여 이벤트를 확장하거나
                    // isKTmember 여부를 판별하는 다른 로직이 필요합니다.
                    // 여기서는 subscriberSignedUp.getIsKt() 메서드가 있다고 가정합니다.
                    newPoint.setIsKTmember(subscriberSignedUp.getIsKt()); // <-- 이벤트에서 isKTmember 정보 가져오기
                    newPoint.setCurrentPoints(0); 
                    return newPoint;
                });

                // 4. 핵심 BIZ 로직: 회원가입 시 초기 포인트 지급 (KT 멤버십 여부에 따라 다름)
                int pointsToGrant;
                if (point.getIsKTmember() != null && point.getIsKTmember()) { // isKTmember가 null이 아니고 true인 경우
                    pointsToGrant = 5000;
                    System.out.println("##### Point BIZ Logic: User " + point.getUserId() + " is a KT member. Granting " + pointsToGrant + " points.\n");
                } else {
                    pointsToGrant = 1000;
                    System.out.println("##### Point BIZ Logic: User " + point.getUserId() + " is NOT a KT member. Granting " + pointsToGrant + " points.\n");
                }
                // point.getCurrentPoints()는 해당 사용자가 현재 가지고 있는 포인트를 가져옵니다.
                // 여기에 계산된 pointsToGrant 값을 더하여 새로운 현재 포인트로 설정합니다.
                point.setCurrentPoints(point.getCurrentPoints() + pointsToGrant);
                System.out.println("##### Point BIZ Logic: User " + point.getUserId() + " has total current points: " + point.getCurrentPoints() + "\n");
                // 최종적으로 업데이트된 사용자별 현재 총 포인트를 로그로 출력합니다.

                // 변경된 Point 엔티티를 데이터베이스에 저장 (영속화)
                // pointRepository.save(point) 메서드를 호출하여 위의 setCurrentPoints()로 변경된 내용을
                // 데이터베이스에 반영하고 저장합니다. @Transactional 어노테이션 덕분에 이 변경사항은
                // 트랜잭션이 성공적으로 커밋될 때 데이터베이스에 영구적으로 기록됩니다.
                pointRepository.save(point);
                System.out.println("##### Point BIZ Logic: Point entity saved for user " + point.getUserId() + "\n");
                // 포인트 엔티티가 성공적으로 저장되었음을 로그로 알립니다.

                // 도메인 이벤트 발행: PointsGranted
                // 포인트 지급이 성공적으로 이루어졌음을 시스템 내 다른 마이크로서비스들에게 알리기 위해
                // 'PointsGranted'라는 이벤트를 생성합니다. 이 이벤트는 포인트 지급의 "사실"을 담습니다.
                PointsGranted pointsGranted = new PointsGranted(point);
                pointsGranted.setUserId(point.getUserId()); 
                pointsGranted.setPointsGrantedAmount(point.getCurrentPoints()); 
                pointsGranted.publishAfterCommit();
                // 이 이벤트는 주로 'PointInquiryViewHandler'와 같은 다른 이벤트 핸들러가 구독하여
                // 'PointInquiry' (조회 모델)를 업데이트하는 데 사용될 것입니다.
                System.out.println("##### Event Published: PointsGranted for user " + point.getUserId() + " with amount " + point.getCurrentPoints() + "\n");
                // 이벤트 발행 성공을 로그로 출력합니다.
                
            } catch (Exception e) {
                System.err.println("##### Error processing SubscriberSignedUp event: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    // TODO: 구독 신청 시 포인트 차감 BIZ 로직을 위한 Consumer 함수를 여기에 추가해야 합니다.
    /*
    @Bean
    public Consumer<AccessRequestedWithPoints> wheneverAccessRequestedWithPoints() {
        // ... (이전 코드와 동일, 주석 처리된 상태 유지)
    }
    */
}
