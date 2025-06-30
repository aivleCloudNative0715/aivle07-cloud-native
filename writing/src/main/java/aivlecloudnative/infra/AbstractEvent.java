package aivlecloudnative.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.Instant; 


//<<< Clean Arch / Outbound Adaptor
@Component
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON 직렬화 시 null 값 필드 제외
@JsonIgnoreProperties(ignoreUnknown = true) // JSON 역직렬화 시 알 수 없는 필드 무시
public abstract class AbstractEvent { 

    private static StreamBridge streamBridge;

    @Autowired
    public static void setStreamBridge(StreamBridge streamBridge) {
        AbstractEvent.streamBridge = streamBridge;
    }

    private String eventType;
    private Long timestamp;

    // ObjectMapper는 한 번만 생성하여 재사용
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new JavaTimeModule());


    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = Instant.now().toEpochMilli();
    }

    public void publish() {
        String destination = "aivlecloudnative"; 
        if (streamBridge == null) {
            throw new IllegalStateException("StreamBridge is not initialized. Ensure AbstractEvent is a Spring @Component and context is fully loaded.");
        }

        try {
            streamBridge.send(
                destination,
                MessageBuilder.withPayload(this.toJson())
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", getEventType())
                    .build()
            );
        } catch (Exception e) {

            System.err.println("Error publishing event " + getEventType() + ": " + e.getMessage());
            throw new RuntimeException("Failed to publish event: " + getEventType(), e);
        }
    }

    public void publishAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {

                    if (status == TransactionSynchronization.STATUS_COMMITTED) { 
                        AbstractEvent.this.publish();
                    } else {
                        System.out.println("DEBUG - Event " + getEventType() + " not published due to transaction status: " + status);
                    }
                }
            }
        );
    }

    // --- Getter & Setter ---
    public String getEventType() {
        return eventType;
    }

    protected void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting event to JSON for " + getEventType() + ": " + e.getMessage());
            throw new RuntimeException("Failed to convert event to JSON string for " + getEventType(), e);
        }
    }
}
//>>> Clean Arch / Outbound Adaptor