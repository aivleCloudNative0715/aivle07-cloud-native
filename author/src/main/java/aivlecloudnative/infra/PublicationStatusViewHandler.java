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
public class PublicationStatusViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private PublicationStatusRepository publicationStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPublicationInfoCreationRequested_then_CREATE_1(
        @Payload PublicationInfoCreationRequested publicationInfoCreationRequested
    ) {
        try {
            if (!publicationInfoCreationRequested.validate()) return;

            // view 객체 생성
            PublicationStatus publicationStatus = new PublicationStatus();
            // view 객체에 이벤트의 Value 를 set 함
            publicationStatus.setManuscriptId(
                publicationInfoCreationRequested.getManuscriptId()
            );
            publicationStatus.setStatus(
                publicationInfoCreationRequested.getStatus()
            );
            // view 레파지 토리에 save
            publicationStatusRepository.save(publicationStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenAutoPublished_then_UPDATE_1(
        @Payload AutoPublished autoPublished
    ) {
        try {
            if (!autoPublished.validate()) return;
            // view 객체 조회

            List<PublicationStatus> publicationStatusList = publicationStatusRepository.findByManuscriptId(
                autoPublished.getManuscriptId()
            );
            for (PublicationStatus publicationStatus : publicationStatusList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                publicationStatus.setStatus(autoPublished.getStatus());
                // view 레파지 토리에 save
                publicationStatusRepository.save(publicationStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
