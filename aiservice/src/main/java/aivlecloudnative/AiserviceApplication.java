package aivlecloudnative;

// 불필요한 import 제거 (KafkaProcessor는 더 이상 사용하지 않음)
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
// import org.springframework.cloud.stream.annotation.EnableBinding; // 제거
import org.springframework.context.ApplicationContext; // 유지 (SpringApplication.run 반환값 때문)

@SpringBootApplication
// @EnableBinding(KafkaProcessor.class) // 이 줄을 제거합니다.
@EnableFeignClients
public class AiserviceApplication {

    // ApplicationContext를 static으로 유지하는 것은 권장되지 않지만,
    // 현재 AbstractEvent에서 사용하고 있으므로 일단 유지하고,
    // 가능하다면 AbstractEvent의 구조를 변경하여 DI를 사용하도록 리팩토링하는 것이 좋습니다.
    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(AiserviceApplication.class, args);
    }
}