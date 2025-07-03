package aivlecloudnative.infra;

import aivlecloudnative.domain.BookWork;
import aivlecloudnative.domain.BookWorkRepository;
import aivlecloudnative.domain.PublicationRequested;
import aivlecloudnative.domain.AutoPublished;

import aivlecloudnative.external.AIServiceSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // import 유지
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional; // Optional 임포트 유지

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Transactional // 이 어노테이션의 트랜잭션 경계를 주의 깊게 관리해야 합니다.
public class PolicyHandler {
    private static final Logger log = LoggerFactory.getLogger(PolicyHandler.class);

    @Autowired
    BookWorkRepository bookWorkRepository;

    @Autowired
    StreamBridge streamBridge;

    @Autowired
    AIServiceSystem aiServiceSystem; // AIServiceSystem 주입 추가

    private final ObjectMapper objectMapper;

    public PolicyHandler() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * publicationRequestedSubscriber: 집필 관리에서 발행하는
     * 출간신청됨(publicationRequested) 이벤트를 구독
     */
    @Bean
    public Consumer<String> publicationRequestedSubscriber() {
        return message -> {
            try {
                log.info("##### Received Raw Message (PublicationRequested): " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"PublicationRequested".equals(eventType)) {
                    log.info("##### Skipping event for publicationRequestedSubscriber, type mismatch: Expected PublicationRequested, Got " + eventType);
                    return;
                }

                PublicationRequested publicationRequested = objectMapper.treeToValue(jsonNode, PublicationRequested.class);
                log.info("##### Transformed PublicationRequested Event: {}", publicationRequested);

                // 핵심 비즈니스 로직을 Mono.fromCallable로 감싸 비동기 처리
                Mono.fromCallable(() -> {
                    // Assuming bookWorkRepository.findByManuscriptId returns Optional<BookWork>
                    Optional<BookWork> optionalBookWork = bookWorkRepository.findByManuscriptId(publicationRequested.getManuscriptId());

                    if (optionalBookWork.isPresent()) {
                        BookWork existingBookWork = optionalBookWork.get();
                        log.warn("manuscriptId {} already has been processed. Skipping. Existing BookWork ID: {}",
                                publicationRequested.getManuscriptId(), existingBookWork.getId());
                        return Mono.empty(); // 이미 처리된 경우 Mono.empty() 반응형 스트림으로 반환
                    } else {
                        // BookWork 객체 생성 방식 수정: static 팩토리 메서드 호출
                        BookWork newBookWork = BookWork.createRequestedBookWork(
                                publicationRequested.getManuscriptId(),
                                publicationRequested.getTitle(),
                                publicationRequested.getContent(),
                                publicationRequested.getSummary(),
                                publicationRequested.getAuthorName(),
                                publicationRequested.getKeywords(),
                                publicationRequested.getAuthorId()
                                // BookWork.createRequestedBookWork 내부에서 상태 설정
                        );
                        bookWorkRepository.save(newBookWork); // 첫 번째 DB 저장
                        log.info("##### [Step 2] BookWork created and initialized (ID: {}).", newBookWork.getId());

                        // AI 서비스 호출은 Mono를 반환하므로, 이를 다시 flatMap으로 체인해야 합니다.
                        // Mono.fromCallable 내부에서는 블로킹 연산만 하는 것이 일반적입니다.
                        // 따라서 이 Mono<AIResponse>를 호출하고 그 결과를 기다릴 수 있도록 Mono.block()을 사용하거나
                        // 이 전체 블록을 Mono 체인으로 바꿔야 합니다.
                        // 여기서는 Mono.fromCallable 안에서 AI 호출을 동기적으로 처리 (block() 사용)
                        // 단, 이렇게 하면 Schedulers.boundedElastic() 스레드가 블로킹됩니다.
                        // 더 좋은 방법은 PolicyHandler 전체를 Reactive하게 만드는 것이지만,
                        // 현재 구조상 가장 빠르게 에러를 해결하는 방향으로 일단 block()을 추가합니다.
                        // 이 부분은 나중에 Mono.fromCallable 바깥으로 빼내어 flatMap으로 연결하는 것을 재고려해야 합니다.

                        AIServiceSystem.AIResponse aiResponse = aiServiceSystem.callGPTApi(
                            newBookWork.getManuscriptId(),
                            newBookWork.getTitle(),
                            newBookWork.getContent(),
                            newBookWork.getSummary(),
                            newBookWork.getAuthorName(),
                            newBookWork.getKeywords(),
                            newBookWork.getAuthorId()
                        ).block(); // <--- 여기서 .block()을 사용해야 컴파일 에러가 해결됩니다.

                        if (aiResponse == null) {
                            log.error("##### AI service call returned null for BookWork ID: {}", newBookWork.getId());
                            // AI 서비스 실패 시 처리 로직 (예: BookWork 상태 변경)
                            // newBookWork.failAiProcessing("AI service returned null");
                            // bookWorkRepository.save(newBookWork);
                            return Mono.empty(); // 실패 시 스트림 종료
                        }

                        log.info("##### [Step 5] AI 서비스 응답 성공적으로 수신 (BookWork ID: {}): {}", newBookWork.getId(), aiResponse);

                        newBookWork.completeAiProcessing(
                                aiResponse.getCoverImageUrl(),
                                aiResponse.getEbookUrl(),
                                aiResponse.getCategory(),
                                aiResponse.getPrice());
                        bookWorkRepository.save(newBookWork); // 두 번째 DB 저장 (업데이트)

                        AutoPublished autoPublishedEvent = AutoPublished.builder()
                                .id(newBookWork.getId())
                                .manuscriptId(newBookWork.getManuscriptId())
                                .title(newBookWork.getTitle())
                                .content(newBookWork.getContent())
                                .summary(newBookWork.getSummary())
                                .keywords(newBookWork.getKeywords())
                                .authorId(newBookWork.getAuthorId())
                                .authorName(newBookWork.getAuthorName())
                                .coverImageUrl(newBookWork.getCoverImageUrl())
                                .ebookUrl(newBookWork.getEbookUrl())
                                .category(newBookWork.getCategory())
                                .price(newBookWork.getPrice())
                                .status(newBookWork.getStatus())
                                .build();

                        // 이벤트를 보내는 부분은 트랜잭션 커밋 이후에 실행되어야 하지만,
                        // StreamBridge는 일반적으로 즉시 전송을 시도합니다.
                        // 트랜잭션 후 발행을 위해서는 ApplicationEventPublisher 또는
                        // @TransactionalEventListener 사용을 고려할 수 있습니다.
                        streamBridge.send("autoPublishedPublisher-out-0", autoPublishedEvent);
                        log.info("##### [Step 6-1] AutoPublished Event published (BookWork ID: {}).", newBookWork.getId());

                        return Mono.just(autoPublishedEvent); // 결과 래핑
                    }
                })
                .flatMap(monoResult -> monoResult) // Mono.empty() 또는 Mono.just()를 받아서 Mono<T>로 플랫맵
                                                 // 이 부분이 이제 필요 없음. Mono.fromCallable 내부에서 Mono를 반환할 때.
                                                 // 즉, Mono.fromCallable 자체의 반환 타입이 Mono<Mono<T>>가 될 수 있음.
                                                 // 아래 .subscribe() 부분에서 다시 .subscribe() 호출 방지.
                .subscribeOn(Schedulers.boundedElastic()) // 블로킹 I/O 작업을 위한 스케줄러 (DB, AI block())
                .subscribe(
                        result -> {
                            // 성공 시 처리 로직
                            if (result instanceof AutoPublished) {
                                log.info("##### PublicationRequested event processing completed successfully for BookWork ID: {}", ((AutoPublished)result).getId());
                            } else {
                                log.info("##### PublicationRequested event processing completed (skipped).");
                            }
                        },
                        error -> log.error("Error processing PublicationRequested event asynchronously: {}", error.getMessage(), error)
                );

            } catch (Exception e) {
                log.error("##### Error processing PublicationRequested event (initial parsing/setup): {}", e.getMessage(), e);
            }
        };
    }
}