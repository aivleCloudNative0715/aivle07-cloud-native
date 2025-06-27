package aivlecloudnative.infra;

import aivlecloudnative.config.kafka.KafkaProcessor;
import aivlecloudnative.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookViewRepository bookViewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AutoPublished'"
    )
    public void wheneverAutoPublished_RegisterNewBook(
        @Payload AutoPublished autoPublished
    ) {
        AutoPublished event = autoPublished;
        System.out.println(
            "\n\n##### listener RegisterNewBook : " + autoPublished + "\n\n"
        );

        // Sample Logic //
        Book.registerNewBook(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AccessRequestedAsSubscriber'"
    )
    public void wheneverAccessRequestedAsSubscriber_BookView(
        @Payload AccessRequestedAsSubscriber accessRequestedAsSubscriber
    ) {
        AccessRequestedAsSubscriber event = accessRequestedAsSubscriber;
        System.out.println(
            "\n\n##### listener BookView : " +
            accessRequestedAsSubscriber +
            "\n\n"
        );

        // Sample Logic //
        Book.bookView(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PointsDeducted'"
    )
    public void wheneverPointsDeducted_BookView(
        @Payload PointsDeducted pointsDeducted
    ) {
        PointsDeducted event = pointsDeducted;
        System.out.println(
            "\n\n##### listener BookView : " + pointsDeducted + "\n\n"
        );

        // Sample Logic //
        Book.bookView(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
