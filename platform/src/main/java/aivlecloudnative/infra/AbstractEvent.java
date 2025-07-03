package aivlecloudnative.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // JSON 역직렬화 시 알 수 없는 필드를 무시
public abstract class AbstractEvent {

    // 이벤트의 고유 ID
    private String eventId;

    // 이벤트 발생 시간 (ISO 8601 형식으로 직렬화/역직렬화 권장)
    private Long timestamp;

    // 이벤트 타입 (구체적인 이벤트 클래스 이름 또는 특정 이벤트 유형 식별자)
    // 이 필드를 통해 consumer에서 어떤 종류의 이벤트인지 식별 가능
    private String eventType;

    public AbstractEvent() {
        this.eventId = UUID.randomUUID().toString(); // 이벤트 생성 시 고유 ID 자동 할당
        this.timestamp = System.currentTimeMillis();       // 현재 시간 자동 할당
        this.eventType = this.getClass().getSimpleName(); // 이벤트 클래스 이름을 기본 이벤트 타입으로 설정
    }

    // JSON 직렬화를 위한 유틸리티 메서드
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 처리를 위해 모듈 등록
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // 날짜를 타임스탬프가 아닌 ISO 형식으로
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting event to JSON", e);
        }
    }

    // JSON 역직렬화를 위한 static 유틸리티 메서드 (제네릭 사용)
    public static <T extends AbstractEvent> T fromJson(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to event", e);
        }
    }

    @Override
    public String toString() {
        return "AbstractEvent{" +
                "eventId='" + eventId + '\'' +
                ", timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}