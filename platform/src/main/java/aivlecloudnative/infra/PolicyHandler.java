package aivlecloudnative.infra;

import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.domain.Book;
import aivlecloudnative.domain.BookRepository;
// import aivlecloudnative.domain.BookViewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Consumer;

//<<< Clean Arch / Inbound Adaptor
@Component
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;
    // @Autowired
    // BookViewRepository bookViewRepository; // <-- 이 필드는 아직 구현되지 않았습니다.
    @Autowired
    StreamBridge streamBridge;

    /**
     * AI 서비스에서 발행하는 자동 출간됨 이벤트를 구독하여 신규 도서를 등록
     */
    @Bean
    public Consumer<String> autoPublishedEventsIn() {
        return message -> {
            try {
                System.out.println("##### Received AutoPublished Event : " + message);

                // 1. JSON 메시지를 AutoPublished 이벤트 객체로 역직렬화
                AutoPublished autoPublished = AutoPublished.fromJson(message, AutoPublished.class);

                System.out.println("##### Transformed AutoPublished Event: " + autoPublished.getEventType() + " - AI Service ID: " + autoPublished.getId());

                // 2. AutoPublished 이벤트 데이터를 사용하여 Book 엔티티 생성
                Book newBook = new Book();
                newBook.setTitle(autoPublished.getTitle());
                newBook.setSummary(autoPublished.getSummary());
                newBook.setAuthorName(autoPublished.getAuthorName());
                newBook.setCategory(autoPublished.getCategory());
                newBook.setCoverImageUrl(autoPublished.getCoverImageUrl());
                newBook.setEbookUrl(autoPublished.getEbookUrl());
                newBook.setPrice(autoPublished.getPrice());
                // viewCount는 Book 생성자에서 0L으로 자동 초기화됩니다.

                // 3. Book 엔티티를 데이터베이스에 저장
                // save() 메서드 호출 시, @GeneratedValue에 따라 DB에서 새로운 ID가 할당
                bookRepository.save(newBook);

                System.out.println("##### New Book registered successfully: " + newBook.getTitle() + " (Book ID: " + newBook.getId() + ")");

            } catch (Exception e) {
                System.err.println("##### Error processing AutoPublished event: " + e.getMessage());
                e.printStackTrace();
                // 에러 처리 로직
            }
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
