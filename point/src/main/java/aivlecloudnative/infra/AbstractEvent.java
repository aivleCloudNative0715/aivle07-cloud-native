package aivlecloudnative.infra;

import aivlecloudnative.PointApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.context.ApplicationEventPublisher; 

//<<< Clean Arch / Outbound Adaptor
public class AbstractEvent {

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

    // Spring Cloud Stream 3.x의 Supplier 함수를 통해 이벤트를 발행하도록 변경
    public void publish() {
        // ApplicationContext에서 ApplicationEventPublisher를 가져와 이벤트를 발행합니다.
        // Spring Cloud Stream은 ApplicationEvent를 자동으로 Kafka로 발행할 수 있습니다.
        // 이를 위해서는 pom.xml에 spring-cloud-stream-binder-kafka-streams 또는 spring-cloud-stream-binder-kafka가 필요합니다.
        // 그리고 application.yml에 spring.cloud.stream.bindings.<functionName>-out-0: destination: topicName 설정이 필요합니다.

        ApplicationEventPublisher publisher = PointApplication.applicationContext.getBean(
            ApplicationEventPublisher.class
        );
        publisher.publishEvent(this); // 현재 이벤트를 Spring Application Event로 발행
    }

    public void publishAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    // 트랜잭션 커밋 후에만 이벤트 발행 (이전과 동일한 로직)
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