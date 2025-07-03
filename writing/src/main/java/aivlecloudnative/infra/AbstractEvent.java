package aivlecloudnative.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = aivlecloudnative.domain.ManuscriptRegistered.class, name = "ManuscriptRegistered"),
    @JsonSubTypes.Type(value = aivlecloudnative.domain.ManuscriptSaved.class, name = "ManuscriptSaved"),
    @JsonSubTypes.Type(value = aivlecloudnative.domain.PublicationRequested.class, name = "PublicationRequested")
})
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractEvent {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new JavaTimeModule());

    private Long id;
    private String eventType;
    private Long timestamp;


    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
        this.timestamp = System.currentTimeMillis();
        this.setEventType(this.getClass().getSimpleName());
    }

    protected void setEventType(String eventType) {
        this.eventType = eventType;
    }

    // validate() 메서드 유지
    public boolean validate() {
        return getEventType() != null && getEventType().equals(this.getClass().getSimpleName());
    }

    // toJson() 메서드 유지 (이벤트 객체를 JSON 문자열로 변환하는 역할)
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