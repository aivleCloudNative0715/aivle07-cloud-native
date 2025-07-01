// src/main/java/aivlecloudnative/domain/EventOutboxSaver.java
package aivlecloudnative.domain; // domain 패키지에 위치

import aivlecloudnative.infra.AbstractEvent; // AbstractEvent는 infra 패키지에 있다고 가정합니다.
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // LocalDateTime 지원
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID; // UUID 임포트

@Service
public class EventOutboxSaver {

    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventOutboxSaver(OutboxMessageRepository outboxMessageRepository) {
        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule()); // LocalDateTime 지원
    }

    // 이 메서드는 호출하는 트랜잭션과 동일하게 동작합니다.
    @Transactional
    public void save(AbstractEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String eventId = UUID.randomUUID().toString(); // 이벤트의 고유 ID 생성

            // OutboxMessage 생성자를 사용하여 객체 생성
            OutboxMessage outboxMessage = new OutboxMessage(
                eventId,
                event.getClass().getSimpleName(), // 이벤트 클래스 이름
                payload
            );

            outboxMessageRepository.save(outboxMessage);
            System.out.println("Event saved to Outbox: " + outboxMessage.getEventType() + " with Event ID: " + eventId);

        } catch (Exception e) {
            System.err.println("Failed to save event to Outbox: " + event.getClass().getSimpleName() + " - " + e.getMessage());
            throw new RuntimeException("Failed to save event to Outbox", e);
        }
    }
}