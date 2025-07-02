package aivlecloudnative.infra;

import aivlecloudnative.domain.BookInfo;
import aivlecloudnative.domain.BookInfoRepository;
import aivlecloudnative.domain.NewBookRegistered;
import aivlecloudnative.domain.Point;
import aivlecloudnative.domain.PointRepository;
import aivlecloudnative.domain.PointsGranted;
import aivlecloudnative.domain.UserSignedUp;
import aivlecloudnative.domain.AccessRequestedWithPoints;
import aivlecloudnative.domain.PointsDeducted;
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
    private final BookInfoRepository bookInfoRepository;

    public PolicyHandler(PointRepository pointRepository, BookInfoRepository bookInfoRepository) {
        this.pointRepository = pointRepository;
        this.bookInfoRepository = bookInfoRepository;
    }

    @Bean
    public Function<Flux<Message<UserSignedUp>>, Flux<Message<PointsGranted>>> userSignedUpSubscriber() {
        return userSignedUpMessageFlux -> userSignedUpMessageFlux
                .flatMap(message -> {
                    UserSignedUp userSignedUp = message.getPayload();
                    log.info("Received UserSignedUp event: {}", userSignedUp);

                    return Mono.fromCallable(() -> pointRepository.findByUserId(userSignedUp.getUserId()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(optionalPoint -> {
                                if (optionalPoint.isPresent()) {
                                    Point existingPoint = optionalPoint.get();
                                    log.warn("User {} already has points (ID: {}). Skipping initial grant.",
                                            userSignedUp.getUserId(), existingPoint.getId());
                                    return Mono.empty();
                                } else {
                                    Point newPoint = Point.createInitialPoint(userSignedUp.getUserId(), userSignedUp.getIsKT());
                                    pointRepository.save(newPoint);
                                    log.info("Granted {} points to user {} (Initial Point ID: {})",
                                            newPoint.getCurrentPoints(), newPoint.getUserId(), newPoint.getId());

                                    PointsGranted pointsGrantedEvent = new PointsGranted();
                                    pointsGrantedEvent.setId(newPoint.getId());
                                    pointsGrantedEvent.setUserId(newPoint.getUserId());
                                    pointsGrantedEvent.setCurrentPoints(newPoint.getCurrentPoints());
                                    pointsGrantedEvent.setGrantedPoints(newPoint.getIsKTmember() ? 5000L : 1000L);

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

    @Bean
    public Function<Flux<Message<NewBookRegistered>>, Mono<Void>> newBookRegisteredSubscriber() {
        return newBookRegisteredMessageFlux -> newBookRegisteredMessageFlux
                .flatMap(message -> {
                    NewBookRegistered newBookRegistered = message.getPayload();
                    log.info("Received NewBookRegistered event: {}", newBookRegistered);

                    return Mono.fromCallable(() -> bookInfoRepository.findByBookId(newBookRegistered.getBookId()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(optionalBookInfo -> {
                                if (optionalBookInfo.isPresent()) {
                                    BookInfo existingBookInfo = optionalBookInfo.get();
                                    log.warn("Book {} (ID: {}) already exists in BookInfo DB. Skipping.",
                                            newBookRegistered.getBookId(), existingBookInfo.getId());
                                    return Mono.empty();
                                } else {
                                    BookInfo bookInfo = BookInfo.builder()
                                            .bookId(newBookRegistered.getBookId())
                                            .price(newBookRegistered.getPrice())
                                            .build();
                                    bookInfoRepository.save(bookInfo);
                                    log.info("Saved new book info: {} with price {}",
                                            bookInfo.getBookId(), bookInfo.getPrice());
                                    return Mono.empty();
                                }
                            });
                })
                .then();
    }

    @Bean
    public Function<Flux<Message<AccessRequestedWithPoints>>, Flux<Message<PointsDeducted>>> accessRequestedWithPointsSubscriber() {
        return accessRequestedMessageFlux -> accessRequestedMessageFlux
                .flatMap(message -> {
                    AccessRequestedWithPoints accessRequest = message.getPayload();
                    log.info("Received AccessRequestedWithPoints event: {}", accessRequest);

                    final String userId = accessRequest.getUserId();
                    final String bookId = accessRequest.getBookId();

                    return Mono.fromCallable(() -> bookInfoRepository.findByBookId(bookId))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(optionalBookInfo -> {
                                if (optionalBookInfo.isEmpty()) {
                                    log.warn("BookInfo for bookId {} not found. Cannot deduct points.", bookId);
                                    return Mono.empty();
                                }
                                Long requiredPoints = optionalBookInfo.get().getPrice();

                                return Mono.fromCallable(() -> pointRepository.findByUserId(userId))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .flatMap(optionalPoint -> {
                                            if (optionalPoint.isEmpty()) {
                                                log.warn("Points for userId {} not found. Cannot deduct points.", userId);
                                                return Mono.empty();
                                            }

                                            Point userPoint = optionalPoint.get();
                                            if (userPoint.getCurrentPoints() < requiredPoints) {
                                                log.warn("User {} has insufficient points ({} current, {} required) for book {}.",
                                                        userId, userPoint.getCurrentPoints(), requiredPoints, bookId);
                                                return Mono.error(new InsufficientPointsException("Insufficient points for user " + userId));
                                            }

                                            userPoint.setCurrentPoints(userPoint.getCurrentPoints() - requiredPoints);
                                            pointRepository.save(userPoint);
                                            log.info("Deducted {} points from user {} for book {}. Remaining points: {}",
                                                    requiredPoints, userId, bookId, userPoint.getCurrentPoints());

                                            PointsDeducted pointsDeductedEvent = new PointsDeducted();
                                            pointsDeductedEvent.setId(userPoint.getId());
                                            pointsDeductedEvent.setUserId(userId);
                                            pointsDeductedEvent.setBookId(bookId);
                                            pointsDeductedEvent.setDeductedPoints(requiredPoints);
                                            pointsDeductedEvent.setCurrentPoints(userPoint.getCurrentPoints());

                                            return Mono.just(MessageBuilder.withPayload(pointsDeductedEvent)
                                                    .setHeader("type", "PointsDeducted")
                                                    .build());
                                        })
                                        .onErrorResume(InsufficientPointsException.class, e -> {
                                            log.error("Point deduction failed for user {} and book {}: {}", userId, bookId, e.getMessage());
                                            return Mono.empty();
                                        });
                            });
                });
    }

    @Bean
    public Function<Flux<Message<PointsDeducted>>, Flux<Message<PointsDeducted>>> pointsDeductedPublisher() {
        return flux -> flux;
    }
}