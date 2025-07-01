package aivlecloudnative;

import aivlecloudnative.infra.AbstractEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

// Jackson 관련 임포트
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication
@EnableFeignClients
public class PointApplication {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(PointApplication.class, args);
    }

    @Bean
    public Object setAbstractEventStreamBridge(StreamBridge streamBridge) {
        AbstractEvent.setStreamBridge(streamBridge);
        return new Object();
    }

    /**
     * Java 8 Date/Time API (LocalDateTime)를 JSON으로 직렬화/역직렬화하기 위한 ObjectMapper 빈 설정.
     * jackson-datatype-jsr310 모듈을 등록하여 이 타입들을 올바르게 처리하도록 합니다.
     * 이 빈이 등록되면 Spring Boot의 기본 ObjectMapper도 이 설정을 사용하게 됩니다.
     *
     * @return 커스터마이징된 ObjectMapper 인스턴스
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
        // 날짜/시간을 ISO 8601 형식의 문자열로 직렬화하도록 설정 (예: "2025-06-30T21:21:19.994")
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // AbstractEvent에 ObjectMapper를 주입하기 위해 static setter 호출
        AbstractEvent.setObjectMapper(objectMapper); // <-- 이 줄을 추가합니다.

        return objectMapper;
    }
}
