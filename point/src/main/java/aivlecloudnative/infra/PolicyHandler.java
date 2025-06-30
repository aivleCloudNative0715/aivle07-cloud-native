package aivlecloudnative.infra;

import aivlecloudnative.config.kafka.KafkaProcessor;
import aivlecloudnative.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.naming.NameParser;
//import javax.naming.NameParser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    PointRepository pointRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='SubscriberSignedUp'"
    )
    public void wheneverSubscriberSignedUp_PointPaymant(
        @Payload SubscriberSignedUp subscriberSignedUp
    ) {
        SubscriberSignedUp event = subscriberSignedUp;
        System.out.println(
            "\n\n##### listener PointPaymant : " + subscriberSignedUp + "\n\n"
        );

        // Sample Logic //
        Point.pointPaymant(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AccessRequestedWithPoints'"
    )
    public void wheneverAccessRequestedWithPoints_PointCheck(
        @Payload AccessRequestedWithPoints accessRequestedWithPoints
    ) {
        AccessRequestedWithPoints event = accessRequestedWithPoints;
        System.out.println(
            "\n\n##### listener PointCheck : " +
            accessRequestedWithPoints +
            "\n\n"
        );

        // Sample Logic //

        PointDeductionCommand command = new PointDeductionCommand();
        Point.pointDeduction(command);
    }
}
//>>> Clean Arch / Inbound Adaptor
