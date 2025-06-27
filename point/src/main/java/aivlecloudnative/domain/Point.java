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

    private Boolean isKTmember;

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(
            PointRepository.class
        );
        return pointRepository;
    }

    //<<< Clean Arch / Port Method
    public void pointDeduction() {
        //implement business logic here:

        PointsDeducted pointsDeducted = new PointsDeducted(this);
        pointsDeducted.publishAfterCommit();
    }

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

        /** Example 2:  finding and process
        

        repository().findById(subscriberSignedUp.get???()).ifPresent(point->{
            
            point // do something
            repository().save(point);

            PointsGranted pointsGranted = new PointsGranted(point);
            pointsGranted.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
