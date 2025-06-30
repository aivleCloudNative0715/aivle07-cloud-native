package aivlecloudnative.infra;

import aivlecloudnative.domain.AccessRequestedAsSubscriber;
import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.domain.Book;
import aivlecloudnative.domain.BookRepository;
import aivlecloudnative.domain.BookView;
import aivlecloudnative.domain.BookViewRepository;
import aivlecloudnative.domain.BookViewed;
import aivlecloudnative.domain.PointsDeducted;

import jakarta.transaction.Transactional;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookViewRepository bookViewRepository;

    // @StreamListener(KafkaProcessor.INPUT)
    // public void whatever(@Payload String eventString) {}

    @Bean
    public Consumer<AutoPublished> consumerAutoPublished() {
        return autoPublished -> { // @Payload 어노테이션 없이 파라미터 사용
            System.out.println(
                "\n\n##### listener RegisterNewBook : " + autoPublished + "\n\n"
            );
            Book newBook = Book.registerNewBook(autoPublished);
            bookRepository.save(newBook); // <<< Book.java에서 저장을 제거했으므로, 여기서 명시적으로 저장합니다.

            BookView newBookView = new BookView();
            newBookView.setBookId(newBook.getId());
            newBookView.setTitle(newBook.getTitle());
            newBookView.setAuthorName(newBook.getAuthorName());
            newBookView.setSummary(newBook.getSummary());
            newBookView.setCategory(newBook.getCategory());
            newBookView.setViewCount(0L);
            newBookView.setIsbestseller(false);
            bookViewRepository.save(newBookView); // BookView 저장
        };
    }

    @Bean
    public Consumer<AccessRequestedAsSubscriber> consumerAccessRequested() {
        return accessRequestedAsSubscriber -> {
            System.out.println(
                "\n\n##### listener BookView : " +
                accessRequestedAsSubscriber +
                "\n\n"
            );

            Long bookId = accessRequestedAsSubscriber.getBookId();
            Long userId = accessRequestedAsSubscriber.getUserId();

            bookRepository.findById(bookId).ifPresent(book -> {
                book.increaseViewCount(userId);
                bookRepository.save(book);
            });
        };
    }

    @Bean
    public Consumer<PointsDeducted> consumerPointsDeducted() {
        return pointsDeducted -> {
            System.out.println(
                "\n\n##### listener BookView : " + pointsDeducted + "\n\n"
            );

            Long bookId = pointsDeducted.getBookId();
            Long userId = pointsDeducted.getUserId();

            bookRepository.findById(bookId).ifPresent(book -> {
                book.increaseViewCount(userId);
                bookRepository.save(book);
            });
        };
    }

    @Bean
    public Consumer<BookViewed> consumerBookViewed() {
        return bookViewed -> {
            System.out.println("\n\n##### Listener BookViewed (for Read Model Update) : " + bookViewed.toJson() + "\n\n");

            bookViewRepository.findById(bookViewed.getId()).ifPresentOrElse(
                bookView -> {
                    bookView.updateFrom(bookViewed);
                    bookViewRepository.save(bookView);
                },
                () -> {
                    BookView newBookView = new BookView();
                    newBookView.updateFrom(bookViewed);
                    bookViewRepository.save(newBookView);
                }
            );
        };
    }
}
//>>> Clean Arch / Inbound Adaptor
