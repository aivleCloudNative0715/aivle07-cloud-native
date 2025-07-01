package aivlecloudnative.infra;

import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.domain.AccessRequestedAsSubscriber;
import aivlecloudnative.domain.Book;
import aivlecloudnative.domain.BookRepository;
import aivlecloudnative.domain.BookView;
import aivlecloudnative.domain.BookViewed;
import aivlecloudnative.domain.BookViewRepository;
import aivlecloudnative.domain.PointsDeducted;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//<<< Clean Arch / Inbound Adaptor
@Component
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookViewRepository bookViewRepository;
    @Autowired
    StreamBridge streamBridge;

    private final ObjectMapper objectMapper;

    public PolicyHandler() {
        // ObjectMapper 설정은 생성자에서 한 번만 수행
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * AI 서비스에서 발행하는 자동 출간됨 이벤트를 구독하여 신규 도서를 등록
     */
    @Bean
    public Consumer<String> autoPublishedEventsIn() {
        return message -> {
            try {
                System.out.println("##### Received Raw Message : " + message);

                // 1. 메시지를 JsonNode로 읽어서 eventType 필드만 확인
                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"AutoPublished".equals(eventType)) {
                    System.out.println("##### Skipping event, type mismatch: Expected AutoPublished, Got " + eventType);
                    return; // 자신이 처리할 이벤트가 아니면 스킵
                }

                // 2. 올바른 타입이면 해당 구체적인 이벤트 클래스로 역직렬화
                AutoPublished autoPublished = objectMapper.treeToValue(jsonNode, AutoPublished.class);

                System.out.println("##### Transformed AutoPublished Event: " + autoPublished.getEventType() + " - AI Service ID: " + autoPublished.getId());

                // 3. AutoPublished 이벤트 데이터를 사용하여 Book 엔티티 생성
                Book newBook = new Book();
                newBook.setTitle(autoPublished.getTitle());
                newBook.setSummary(autoPublished.getSummary());
                newBook.setAuthorName(autoPublished.getAuthorName());
                newBook.setCategory(autoPublished.getCategory());
                newBook.setCoverImageUrl(autoPublished.getCoverImageUrl());
                newBook.setEbookUrl(autoPublished.getEbookUrl());
                newBook.setPrice(autoPublished.getPrice());
                // viewCount와 isBestseller는 Book 생성자에서 초기화

                // 4. Book 엔티티를 데이터베이스에 저장
                // save() 메서드 호출 시, @GeneratedValue에 따라 DB에서 새로운 ID가 할당
                bookRepository.save(newBook);

                System.out.println("##### New Book registered successfully: " + newBook.getTitle() + " (Book ID: " + newBook.getId() + ")");

            } catch (Exception e) {
                System.err.println("##### Error processing AutoPublished event: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * 사용자 관리 서버에서 발행하는 구독자로 열람신청됨 이벤트를 구독하여 도서 열람 기록을 처리
     */
    @Bean
    public Consumer<String> accessRequestedAsSubscriberEventsIn() {
        return message -> {
            try {
                System.out.println("##### Received Raw Message : " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"AccessRequestedAsSubscriber".equals(eventType)) {
                    System.out.println("##### Skipping event, type mismatch: Expected AccessRequestedAsSubscriber, Got " + eventType);
                    return;
                }

                AccessRequestedAsSubscriber event = objectMapper.treeToValue(jsonNode, AccessRequestedAsSubscriber.class);

                System.out.println("##### Transformed AccessRequestedAsSubscriber Event: " + event.getEventType() + " - User ID: " + event.getUserId() + ", Book ID: " + event.getBookId());

                processBookView(event.getBookId(), event.getUserId());

            } catch (Exception e) {
                System.err.println("##### Error processing AccessRequestedAsSubscriber event: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * 포인트 서버에서 발행하는 포인트 차감됨 이벤트를 구독하여 도서 열람 기록을 처리
     */
    @Bean
    public Consumer<String> pointsDeductedEventsIn() {
        return message -> {
            try {
                System.out.println("##### Received Raw Message : " + message);

                JsonNode jsonNode = objectMapper.readTree(message);
                String eventType = jsonNode.get("eventType").asText();

                if (!"PointsDeducted".equals(eventType)) {
                    System.out.println("##### Skipping event, type mismatch: Expected PointsDeducted, Got " + eventType);
                    return;
                }

                PointsDeducted event = objectMapper.treeToValue(jsonNode, PointsDeducted.class);

                System.out.println("##### Transformed PointsDeducted Event: " + event.getEventType() + " - User ID: " + event.getUserId() + ", Book ID: " + event.getBookId());

                processBookView(event.getBookId(), event.getUserId());

            } catch (Exception e) {
                System.err.println("##### Error processing PointsDeducted event: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * 도서 열람 공통 처리 로직
     */
    private void processBookView(Long bookId, String userId) {
        // 1. Book 엔티티 업데이트 (totalViewCount, isBestseller)
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setViewCount(book.getViewCount() + 1); // 전체 조회수 증가

            // 베스트셀러 조건 검사 및 업데이트
            if (book.getViewCount() >= 3 && !book.getIsBestseller()) {
                book.setIsBestseller(true);
                System.out.println("##### Book " + book.getTitle() + " (ID: " + book.getId() + ") became a BestSeller!");
            }
            bookRepository.save(book);
            System.out.println("##### Book total viewCount updated for Book ID: " + bookId + ", New Total ViewCount: " + book.getViewCount());

            // 2. BookView 엔티티 업데이트 또는 생성 (personalViewCount, lastViewedAt, firstViewedAt)
            Optional<BookView> optionalBookView = bookViewRepository.findByUserIdAndBookId(userId, bookId);
            BookView bookView;
            if (optionalBookView.isPresent()) {
                bookView = optionalBookView.get();
                bookView.setViewCount(bookView.getViewCount() + 1); // 개인 조회수 증가
                bookView.setLastViewedAt(LocalDateTime.now()); // 마지막 열람 시간 업데이트
                System.out.println("##### BookView updated for User: " + userId + ", Book ID: " + bookId + ", New Personal ViewCount: " + bookView.getViewCount());
            } else {
                bookView = new BookView();
                bookView.setBookId(bookId);
                bookView.setUserId(userId);
                bookView.setViewCount(1L); // 첫 열람이므로 1로 설정
                bookView.setFirstViewedAt(LocalDateTime.now());
                bookView.setLastViewedAt(LocalDateTime.now());
                System.out.println("##### New BookView created for User: " + userId + ", Book ID: " + bookId);
            }
            bookViewRepository.save(bookView);

            // 3. BookViewed 이벤트 발행
            BookViewed bookViewedEvent = new BookViewed(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getSummary(),
                    book.getCategory(),
                    book.getCoverImageUrl(),
                    book.getPrice(),
                    book.getViewCount(),        // totalViewCount
                    bookView.getViewCount(),    // personalViewCount
                    userId
            );
            streamBridge.send("bookViewed-out-0", bookViewedEvent.toJson());
            System.out.println("##### BookViewed event published: " + bookViewedEvent.toJson());

        } else {
            System.err.println("##### Error: Book with ID " + bookId + " not found for view processing.");
            // 특정 도서가 존재하지 않을 경우의 처리 로직 (예: 에러 이벤트 발행)
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
