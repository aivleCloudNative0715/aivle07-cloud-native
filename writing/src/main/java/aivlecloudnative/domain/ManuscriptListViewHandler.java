package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration; // 제거: @Service와 함께 불필요
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aivlecloudnative.domain.ManuscriptRegistered;
import aivlecloudnative.domain.ManuscriptSaved;
import aivlecloudnative.domain.PublicationRequested;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

// @Configuration 제거하고 @Service만 유지합니다.
@Service
@Transactional
public class ManuscriptListViewHandler {

    @Autowired
    private ManuscriptListRepository manuscriptListRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new JavaTimeModule());


    // 메서드 이름을 'eventIn'에서 'manuscriptEventsIn'으로 변경하여 의미를 명확히 하고,
    // Spring Cloud Stream의 바인딩 규칙(함수 이름-in-0)에 더 잘 맞춥니다.
    @Bean
    public Consumer<Message<String>> manuscriptEventsIn() { // 메서드 이름 변경
        return message -> {
            String eventJson = message.getPayload();
            try {
                // 1. 이벤트 타입을 알아낸다
                AbstractEvent genericEvent = OBJECT_MAPPER.readValue(eventJson, AbstractEvent.class);
                String eventType = genericEvent.getEventType();

                System.out.println("ManuscriptListViewHandler: Received event from Kafka (Type: " + eventType + ")");

                // 2. 이벤트 타입에 따라 적절한 구체적인 이벤트 객체로 다시 역직렬화하고 해당 처리 메서드를 호출.

                if ("ManuscriptRegistered".equals(eventType)) {
                    ManuscriptRegistered event = OBJECT_MAPPER.readValue(eventJson, ManuscriptRegistered.class);
                    processManuscriptRegistered(event);
                } else if ("ManuscriptSaved".equals(eventType)) {
                    ManuscriptSaved event = OBJECT_MAPPER.readValue(eventJson, ManuscriptSaved.class);
                    processManuscriptSaved(event);
                } else if ("PublicationRequested".equals(eventType)) {
                    PublicationRequested event = OBJECT_MAPPER.readValue(eventJson, PublicationRequested.class);
                    processPublicationRequested(event);
                }
                else {
                    System.err.println("ManuscriptListViewHandler: Unknown event type received: " + eventType);
                }

            } catch (Exception e) {
                System.err.println("ManuscriptListViewHandler: Error processing event from Kafka: " + e.getMessage());
                e.printStackTrace();

            }
        };
    }


    private void processManuscriptRegistered(ManuscriptRegistered event) {
        System.out.println("ManuscriptListViewHandler: Processing ManuscriptRegistered for ID: " + event.getManuscriptId());

        ManuscriptList manuscriptList = new ManuscriptList();
        manuscriptList.setManuscriptId(event.getManuscriptId());
        manuscriptList.setAuthorId(event.getAuthorId());
        manuscriptList.setTitle(event.getTitle());
        manuscriptList.setContent(event.getContent());
        manuscriptList.setSummary(event.getSummary());
        manuscriptList.setKeywords(event.getKeywords());
        manuscriptList.setStatus(event.getStatus());
        manuscriptList.setLastModifiedAt(event.getLastModifiedAt());

        manuscriptListRepository.save(manuscriptList);
        System.out.println("ManuscriptListViewHandler: ManuscriptList created for ID: " + event.getManuscriptId());
    }


    private void processManuscriptSaved(ManuscriptSaved event) {
        System.out.println("ManuscriptListViewHandler: Processing ManuscriptSaved for ID: " + event.getManuscriptId());

        Optional<ManuscriptList> optionalManuscriptList = manuscriptListRepository.findById(event.getManuscriptId());

        ManuscriptList manuscriptList;
        if (optionalManuscriptList.isPresent()) {
            manuscriptList = optionalManuscriptList.get();
        } else {
            System.err.println("ManuscriptListViewHandler Warning: ManuscriptList not found for Saved event ID: " + event.getManuscriptId() + ". Attempting to create new entry.");
            manuscriptList = new ManuscriptList();
            manuscriptList.setManuscriptId(event.getManuscriptId());

        }

        manuscriptList.setAuthorId(event.getAuthorId());
        manuscriptList.setTitle(event.getTitle());
        manuscriptList.setContent(event.getContent());
        manuscriptList.setSummary(event.getSummary());
        manuscriptList.setKeywords(event.getKeywords());
        manuscriptList.setStatus(event.getStatus());
        manuscriptList.setLastModifiedAt(event.getLastModifiedAt());

        manuscriptListRepository.save(manuscriptList);
        System.out.println("ManuscriptListViewHandler: ManuscriptList updated for ID: " + event.getManuscriptId());
    }


    private void processPublicationRequested(PublicationRequested event) {
        System.out.println("ManuscriptListViewHandler: Processing PublicationRequested for ID: " + event.getManuscriptId());

        manuscriptListRepository.findById(event.getManuscriptId()).ifPresentOrElse(
            manuscriptList -> {
                // 리드 모델의 status와 lastModifiedAt만 업데이트
                manuscriptList.setStatus("PUBLICATION_REQUESTED");
                manuscriptList.setLastModifiedAt(LocalDateTime.now()); // 현재 시간으로 업데이트


                manuscriptListRepository.save(manuscriptList);
                System.out.println("ManuscriptListViewHandler: ManuscriptList status updated to PUBLICATION_REQUESTED for ID: " + event.getManuscriptId());
            },
            () -> {
                System.err.println("ManuscriptListViewHandler 오류: PublicationRequested 이벤트 ID: " + event.getManuscriptId() + "에 대한 ManuscriptList를 찾을 수 없습니다. 업데이트할 수 없습니다.");
            }
        );
    }
}