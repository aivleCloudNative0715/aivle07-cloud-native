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
public class ManuscriptListViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private ManuscriptListRepository manuscriptListRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenManuscriptRegisterd_then_CREATE_1(
        @Payload ManuscriptRegisterd manuscriptRegisterd
    ) {
        try {
            if (!manuscriptRegisterd.validate()) return;

            // view 객체 생성
            ManuscriptList manuscriptList = new ManuscriptList();
            // view 객체에 이벤트의 Value 를 set 함
            manuscriptList.setManuscriptId(manuscriptRegisterd.getId());
            manuscriptList.setManuscriptTitle(manuscriptRegisterd.getTitle());
            manuscriptList.setManuscriptContent(
                manuscriptRegisterd.getContent()
            );
            manuscriptList.setManuscriptStatus(manuscriptRegisterd.getStatus());
            manuscriptList.setLastModifiedAt(
                String.valueOf(manuscriptRegisterd.getLastModifiedAt())
            );
            manuscriptList.setAutorId(manuscriptRegisterd.getAuthorId());
            manuscriptList.setAutorId(manuscriptRegisterd.getAuthorId());
            // view 레파지 토리에 save
            manuscriptListRepository.save(manuscriptList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenManuscriptSaved_then_UPDATE_1(
        @Payload ManuscriptSaved manuscriptSaved
    ) {
        try {
            if (!manuscriptSaved.validate()) return;
            // view 객체 조회

            List<ManuscriptList> manuscriptListList = manuscriptListRepository.findByAutorId(
                manuscriptSaved.getAuthorId()
            );
            for (ManuscriptList manuscriptList : manuscriptListList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                manuscriptList.setManuscriptId(manuscriptSaved.getId());
                manuscriptList.setManuscriptTitle(manuscriptSaved.getTitle());
                manuscriptList.setManuscriptContent(
                    manuscriptSaved.getContent()
                );
                manuscriptList.setManuscriptStatus(manuscriptSaved.getStatus());
                manuscriptList.setLastModifiedAt(
                    String.valueOf(manuscriptSaved.getLastModifiedAt())
                );
                // view 레파지 토리에 save
                manuscriptListRepository.save(manuscriptList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
