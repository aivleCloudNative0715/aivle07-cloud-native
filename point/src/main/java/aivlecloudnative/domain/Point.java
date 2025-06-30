package aivlecloudnative.domain;

import aivlecloudnative.PointApplication;
import aivlecloudnative.domain.PointsGranted;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Point_table")
@Data
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer currentPoints;

    private Long userId;

    private Boolean isKTmember;

    // private Boolean isKTmember; // 위에 있어서 주석 처리 완료

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(
            PointRepository.class
        );
        return pointRepository;
    }

    //<<< Clean Arch / Port Method
    public void pointDeduction(PointDeductionCommand command) {
        //implement business logic here:

        PointsDeducted pointsDeducted = new PointsDeducted(this);
        pointsDeducted.publishAfterCommit();
    }

    // TODO: 호출부에서 인자를 넘기고 있다면 아래처럼 오버로딩으로 하나 더 만들어야 합니다.


    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void pointPaymant(SubscriberSignedUp subscriberSignedUp) {
        //implement business logic here:

        /** Example 1:  new item 
        Point point = new Point();
        repository().save(point);

        PointsGranted pointsGranted = new PointsGranted(point);
        pointsGranted.publishAfterCommit();
        */

         // Example 2:  finding and process
         // TODO: subscriberSignedUp.getUserId() 같이 적절한 getter 메서드 작성 필요

        repository().findById(subscriberSignedUp.getId()).ifPresent(point->{

            point.setCurrentPoints(point.getCurrentPoints() + 1000);
            repository().save(point);

            PointsGranted pointsGranted = new PointsGranted(point);
            pointsGranted.publishAfterCommit();

         });
        

    }
    //>>> Clean Arch / Port Method

    // TODO: 아래 메서드들이 PointsGranted, PointsDeducted 클래스에 없다면 추가해 주세요

    public Long getUserId() {
        return userId;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }
    

}
//>>> DDD / Aggregate Root
