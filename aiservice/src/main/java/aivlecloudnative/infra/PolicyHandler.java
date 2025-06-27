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
    BookWorkRepository bookWorkRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PublicationRequested'"
    )
    public void wheneverPublicationRequested_AutoPublicationRequest(
        @Payload PublicationRequested publicationRequested
    ) {
        PublicationRequested event = publicationRequested;
        System.out.println(
            "\n\n##### listener AutoPublicationRequest : " +
            publicationRequested +
            "\n\n"
        );

        // Sample Logic //
        BookWork.autoPublicationRequest(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
