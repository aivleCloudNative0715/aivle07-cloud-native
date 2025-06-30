package aivlecloudnative;

// import aivlecloudnative.config.kafka.KafkaProcessor; // <-- 제거
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
// import org.springframework.cloud.stream.annotation.EnableBinding; // <-- 제거
import org.springframework.context.ApplicationContext; 

@SpringBootApplication
@EnableFeignClients
public class WritingApplication {

    // 정적 필드 유지 (현재 코드의 다른 부분들이 의존하고 있으므로, 필수 마이그레이션에서는 유지)
    // 장기적으로는 이 방식 대신 Spring의 DI를 활용하는 것이 좋습니다.
    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext =
            SpringApplication.run(WritingApplication.class, args);
    }
}