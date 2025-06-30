package aivlecloudnative.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper 임포트
import com.fasterxml.jackson.databind.SerializationFeature; // SerializationFeature 임포트
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // JavaTimeModule 임포트

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter; // <-- 이 임포트가 반드시 있어야 합니다.
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.LocalDateTime;

public class AbstractEvent {

    protected static StreamBridge streamBridge;
    // ObjectMapper 인스턴스를 static 필드로 선언합니다.
    protected static ObjectMapper objectMapper; // <-- 이 필드가 있어야 합니다.

    String eventType;
    private LocalDateTime timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        // BeanUtils.copyProperties는 Aggregate의 모든 속성을 복사하므로 주의해서 사용해야 합니다.
        // 이벤트에 필요한 필드만 Aggregate에서 명시적으로 가져와 설정하는 것이 더 안전합니다.
        // 필요하다면 이 라인을 주석 처리하고 아래 생성자에서 직접 필드를 복사하는 로직을 추가하세요.
        // BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = LocalDateTime.now();
    }

    public static void setStreamBridge(StreamBridge bridge) {
        AbstractEvent.streamBridge = bridge;
    }

    /**
     * Spring Context가 ObjectMapper 빈을 초기화할 때 호출되어
     * AbstractEvent의 static objectMapper 필드를 설정하는 메서드입니다.
     * @param mapper Spring이 관리하는 ObjectMapper 인스턴스
     */
    public static void setObjectMapper(ObjectMapper mapper) { // <-- 이 메서드가 있어야 합니다.
        AbstractEvent.objectMapper = mapper;
    }

    public void publish() {
        if (streamBridge == null) {
            System.err.println("Error: StreamBridge is null. Cannot publish event.");
            return;
        }
        if (objectMapper == null) { // ObjectMapper가 설정되지 않았을 때의 방어 로직 추가
            System.err.println("Error: ObjectMapper is null in AbstractEvent. Cannot publish event.");
            return;
        }

        String outputBindingName = Character.toLowerCase(getEventType().charAt(0)) + getEventType().substring(1) + "-out-0";

        // 이제 새로 생성하지 않고 주입받은 objectMapper를 사용합니다.
        System.out.println("##### Publishing event to: " + outputBindingName + ", Event Type: " + getEventType() + ", JSON: " + this.toJson());
        streamBridge.send(outputBindingName, this);
    }

    public void publishAfterCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() { // <-- 이 부분이 오류 나는 부분일 것입니다.
                    @Override
                    public void afterCompletion(int status) {
                        if (status == TransactionSynchronization.STATUS_COMMITTED) {
                            publish();
                        }
                    }
                }
            );
        } else {
            publish();
        }
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean validate() {
        return getEventType() != null && getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        // 더 이상 여기서 ObjectMapper를 새로 생성하지 않습니다.
        // 주입받은 static objectMapper를 사용합니다.
        if (objectMapper == null) { // 방어 코드: 혹시라도 null이면 런타임 오류 방지
            throw new IllegalStateException("ObjectMapper is not initialized in AbstractEvent.");
        }
        String json = null;

        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // 더 자세한 로그를 위해 RuntimeException 대신 바로 출력
            System.err.println("##### ERROR: JSON format exception during event serialization: " + getEventType() + " - " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스도 함께 출력
            throw new RuntimeException("JSON format exception for event: " + getEventType(), e);
        }
        return json;
    }
}
