package aivlecloudnative.infra;

import aivlecloudnative.domain.Point;
import aivlecloudnative.domain.PointRepository;
import aivlecloudnative.domain.PointsGranted;
import aivlecloudnative.domain.UserSignedUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

@Configuration
public class PolicyHandler {

    private static final Logger log = LoggerFactory.getLogger(PolicyHandler.class);
    private final PointRepository pointRepository;

    public PolicyHandler(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Bean
    public Function<Flux<Message<UserSignedUp>>, Flux<Message<PointsGranted>>> userSignedUpSubscriber() {
        return userSignedUpMessageFlux -> userSignedUpMessageFlux
                .flatMap(message -> {
                    UserSignedUp userSignedUp = message.getPayload();
                    log.info("Received UserSignedUp event: {}", userSignedUp);

                    // JPA 호출은 블로킹 작업이므로, 별도의 스케줄러에서 실행하여 비동기 스트림을 방해하지 않도록 함
                    return Mono.fromCallable(() -> pointRepository.findByUserId(userSignedUp.getUserId()))
                            .subscribeOn(Schedulers.boundedElastic()) // 블로킹 호출을 위한 스케줄러 지정
                            .flatMap(optionalPoint -> {
                                if (optionalPoint.isPresent()) {
                                    Point existingPoint = optionalPoint.get();
                                    log.warn("User {} already has points (ID: {}). Skipping initial grant.",
                                            userSignedUp.getUserId(), existingPoint.getId());
                                    // 이미 포인트가 있는 경우, 빈 Mono를 반환하여 이벤트를 발행하지 않음
                                    return Mono.empty();
                                } else {
                                    // 신규 사용자에게 포인트 지급
                                    Point newPoint = Point.createInitialPoint(userSignedUp.getUserId(), userSignedUp.getIsKT());
                                    pointRepository.save(newPoint); // DB에 포인트 정보 저장
                                    log.info("Granted {} points to user {} (Initial Point ID: {})",
                                            newPoint.getCurrentPoints(), newPoint.getUserId(), newPoint.getId());

                                    // PointsGranted 이벤트 생성
                                    PointsGranted pointsGrantedEvent = new PointsGranted();
                                    pointsGrantedEvent.setId(newPoint.getId());
                                    pointsGrantedEvent.setUserId(newPoint.getUserId());
                                    pointsGrantedEvent.setCurrentPoints(newPoint.getCurrentPoints());
                                    pointsGrantedEvent.setGrantedPoints(newPoint.getIsKTmember() ? 5000L : 1000L);

                                    // Spring Cloud Stream이 반환된 Mono<Message<PointsGranted>>를 감지하고 발행합니다.
                                    return Mono.just(MessageBuilder.withPayload(pointsGrantedEvent)
                                            .setHeader("type", "PointsGranted")
                                            .build());
                                }
                            });
                });
    }

    @Bean
    public Function<Flux<Message<PointsGranted>>, Flux<Message<PointsGranted>>> pointsGrantedPublisher() {
        return flux -> flux;
    }
}