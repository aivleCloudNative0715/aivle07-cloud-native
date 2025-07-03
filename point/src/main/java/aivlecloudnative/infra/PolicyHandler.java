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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Transactional
public class PolicyHandler {

    private static final Logger log = LoggerFactory.getLogger(PolicyHandler.class);

    @Autowired
    PointRepository pointRepository;
    @Autowired
    BookInfoRepository bookInfoRepository;
    @Autowired
    StreamBridge streamBridge;

    private final ObjectMapper objectMapper;

    public PolicyHandler() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    /**
     * userSignedUpSubscriber: 사용자 관리에서 발행하는 회원가입됨(UserSignedUp) 이벤트를 구독
     */
    @Bean
    public Consumer<String> userSignedUpSubscriber() {
        return message -> {
            try {
                log.info("##### Received Raw Message (UserSignedUp): " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"UserSignedUp".equals(eventType)) {
                    log.info("##### Skipping event for userSignedUpSubscriber, type mismatch: Expected UserSignedUp, Got " + eventType);
                    return;
                }

                UserSignedUp userSignedUp = objectMapper.treeToValue(jsonNode, UserSignedUp.class);
                log.info("##### Transformed UserSignedUp Event: {}", userSignedUp);

                Mono.fromCallable(() -> {
                    Optional<Point> optionalPoint = pointRepository.findByUserId(userSignedUp.getUserId());
                    if (optionalPoint.isPresent()) {
                        Point existingPoint = optionalPoint.get();
                        log.warn("User {} already has points (ID: {}). Skipping initial grant.",
                                userSignedUp.getUserId(), existingPoint.getId());
                        return null;
                    } else {
                        Point newPoint = Point.createInitialPoint(userSignedUp.getUserId(), userSignedUp.getIsKt());
                        pointRepository.save(newPoint);
                        log.info("Granted {} points to user {} (Initial Point ID: {})",
                                newPoint.getCurrentPoints(), newPoint.getUserId(), newPoint.getId());

                        PointsGranted pointsGrantedEvent = new PointsGranted();
                        pointsGrantedEvent.setId(newPoint.getId());
                        pointsGrantedEvent.setUserId(newPoint.getUserId());
                        pointsGrantedEvent.setCurrentPoints(newPoint.getCurrentPoints());
                        pointsGrantedEvent.setGrantedPoints(newPoint.getIsKTmember() ? 5000L : 1000L);

                        streamBridge.send("pointsGrantedPublisher-out-0", pointsGrantedEvent);

                        return newPoint;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    result -> {},
                    error -> log.error("Error processing UserSignedUp event asynchronously: {}", error.getMessage(), error)
                );

            } catch (Exception e) {
                log.error("##### Error processing UserSignedUp event: {}", e.getMessage(), e);
            }
        };
    }

    @Bean
    public Function<String, String> pointsGrantedPublisher() {
        return s -> s;
    }

    /**
     * newBookRegisteredSubscriber: 서재 플랫폼에서 발행하는 신규도서등록됨(NewBookRegistered) 이벤트를 구독
     */
    @Bean
    public Consumer<String> newBookRegisteredSubscriber() {
        return message -> {
            try {
                log.info("##### Received Raw Message (NewBookRegistered): " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"NewBookRegistered".equals(eventType)) {
                    log.info("##### Skipping event for newBookRegisteredSubscriber, type mismatch: Expected NewBookRegistered, Got " + eventType);
                    return;
                }

                NewBookRegistered newBookRegistered = objectMapper.treeToValue(jsonNode, NewBookRegistered.class);
                log.info("##### Transformed NewBookRegistered Event: {}", newBookRegistered);

                Mono.fromCallable(() -> {
                    Optional<BookInfo> optionalBookInfo = bookInfoRepository.findByBookId(newBookRegistered.getBookId());
                    if (optionalBookInfo.isPresent()) {
                        BookInfo existingBookInfo = optionalBookInfo.get();
                        log.warn("Book {} (ID: {}) already exists in BookInfo DB. Skipping.",
                                newBookRegistered.getBookId(), existingBookInfo.getId());
                        return null;
                    } else {
                        BookInfo bookInfo = BookInfo.builder()
                                .bookId(newBookRegistered.getBookId())
                                .price(newBookRegistered.getPrice())
                                .build();
                        bookInfoRepository.save(bookInfo);
                        log.info("Saved new book info: {} with price {}",
                                bookInfo.getBookId(), bookInfo.getPrice());
                        return bookInfo;
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    result -> {},
                    error -> log.error("Error processing NewBookRegistered event asynchronously: {}", error.getMessage(), error)
                );

            } catch (Exception e) {
                log.error("##### Error processing NewBookRegistered event: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * accessRequestedWithPointsSubscriber: 사용자 관리에서 발행하는 포인트로 열람 신청함(AccessRequestedWithPoints) 이벤트를 구독
     */
    @Bean
    public Consumer<String> accessRequestedWithPointsSubscriber() {
        return message -> {
            try {
                log.info("##### Received Raw Message (AccessRequestedWithPoints): " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"AccessRequestedWithPoints".equals(eventType)) {
                    log.info("##### Skipping event for accessRequestedWithPointsSubscriber, type mismatch: Expected AccessRequestedWithPoints, Got " + eventType);
                    return;
                }

                AccessRequestedWithPoints accessRequest = objectMapper.treeToValue(jsonNode, AccessRequestedWithPoints.class);
                log.info("##### Transformed AccessRequestedWithPoints Event: {}", accessRequest);

                final String userId = accessRequest.getUserId();
                final String bookId = accessRequest.getBookId();

                Mono.fromCallable(() -> {
                    Optional<BookInfo> optionalBookInfo = bookInfoRepository.findByBookId(bookId);
                    if (optionalBookInfo.isEmpty()) {
                        log.warn("BookInfo for bookId {} not found. Cannot deduct points.", bookId);
                        return null;
                    }
                    Long requiredPoints = optionalBookInfo.get().getPrice();

                    Optional<Point> optionalPoint = pointRepository.findByUserId(Long.valueOf(userId));
                    if (optionalPoint.isEmpty()) {
                        log.warn("Points for userId {} not found. Cannot deduct points.", userId);
                        return null;
                    }

                    Point userPoint = optionalPoint.get();
                    if (userPoint.getCurrentPoints() < requiredPoints) {
                        log.warn("User {} has insufficient points ({} current, {} required) for book {}.",
                                userId, userPoint.getCurrentPoints(), requiredPoints, bookId);
                        throw new InsufficientPointsException("Insufficient points for user " + userId);
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

                    streamBridge.send("pointsDeductedPublisher-out-0", pointsDeductedEvent);

                    return userPoint;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                    result -> {},
                    error -> {
                        if (error instanceof InsufficientPointsException) {
                            log.error("Point deduction failed for user {} and book {}: {}", userId, bookId, error.getMessage());
                        } else {
                            log.error("Error processing AccessRequestedWithPoints event asynchronously: {}", error.getMessage(), error);
                        }
                    }
                );

            } catch (Exception e) {
                log.error("##### Error processing AccessRequestedWithPoints event: {}", e.getMessage(), e);
            }
        };
    }

    @Bean
    public Function<String, String> pointsDeductedPublisher() {
        return s -> s;
    }
}