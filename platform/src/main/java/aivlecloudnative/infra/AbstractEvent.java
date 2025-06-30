package aivlecloudnative.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.BeanUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;

//<<< Clean Arch / Outbound Adaptor
@Component
public class AbstractEvent {

    @Autowired
    private StreamBridge streamBridge;

    String eventType;
    Long timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = System.currentTimeMillis();
    }

    public void publish() {
        String destination = "aivlecloudnative"; // <<< 이벤트를 발행할 Kafka 토픽 이름 (application.yml과 일치)

                try {
            streamBridge.send(
                destination,
                MessageBuilder.withPayload(this.toJson()) // JSON 문자열로 페이로드 설정
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", getEventType()) // 'type' 헤더는 필수
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error publishing event: " + getEventType(), e);
        }
    }

    public void publishAfterCommit() {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                AbstractEvent.this.publish();
            }
        }
    );
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        return json;
    }
}
//>>> Clean Arch / Outbound Adaptor
