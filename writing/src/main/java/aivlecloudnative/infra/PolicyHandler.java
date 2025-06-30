package aivlecloudnative.infra;

// import aivlecloudnative.config.kafka.KafkaProcessor; // <-- 제거
import aivlecloudnative.domain.*; 
import com.fasterxml.jackson.databind.DeserializationFeature; 
import com.fasterxml.jackson.databind.ObjectMapper; 
import javax.naming.NameParser;
import javax.transaction.Transactional; 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.context.annotation.Bean; // @Bean 어노테이션을 위해 추가
import java.util.function.Consumer; // Consumer 인터페이스를 위해 추가


//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    ManuscriptRepository manuscriptRepository; // 현재 whatever() 메서드에서 사용되지 않지만, 다른 정책이 있다면 사용될 수 있으므로 유지

    // --- 이벤트를 처리하는 Consumer Bean ---
    @Bean
    public Consumer<String> whatever() { // 메서드 이름은 그대로 유지하되, Consumer<String> 타입을 반환
        return eventString -> {
            
            // TODO: 수신된 eventString을 파싱하여 필요한 비즈니스 로직을 수행

        };
    }
}
//>>> Clean Arch / Inbound Adaptor