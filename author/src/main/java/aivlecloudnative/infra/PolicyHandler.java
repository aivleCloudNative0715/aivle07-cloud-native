package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class PolicyHandler {

    private final AuthorRepository authorRepository;

    public PolicyHandler(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Kafka에서 String 메시지(이벤트) 받는 Consumer 함수
    @Bean
    public Consumer<String> whatever() {
        return eventString -> {
            // 이벤트 처리 로직 (로그 찍기 예시)
            System.out.println("Received event: " + eventString);
        };
    }
}
