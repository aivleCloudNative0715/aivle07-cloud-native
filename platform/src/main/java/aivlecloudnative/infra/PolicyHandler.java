package aivlecloudnative.infra;

import aivlecloudnative.config.kafka.KafkaProcessor;
import aivlecloudnative.domain.AccessRequestedAsSubscriber;
import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.domain.Book;
import aivlecloudnative.domain.BookRepository;
import aivlecloudnative.domain.BookView;
import aivlecloudnative.domain.BookViewRepository;
import aivlecloudnative.domain.BookViewed;
import aivlecloudnative.domain.PointsDeducted;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookViewRepository bookViewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AutoPublished'"
    )
    public void wheneverAutoPublished_RegisterNewBook(
        @Payload AutoPublished autoPublished
    ) {
        System.out.println(
            "\n\n##### listener RegisterNewBook : " + autoPublished + "\n\n"
        );

        // Sample Logic //
        Book newBook = Book.registerNewBook(autoPublished); // Book 내부에서 저장하므로 여기서는 save 불필요 (현재 Book.java에 맞춰 유지)

        // 새로운 Book이 등록될 때 BookView도 초기 생성
        BookView newBookView = new BookView();
        // Book 객체의 필드를 직접 가져와서 BookView 초기화
        newBookView.setBookId(newBook.getId());
        newBookView.setTitle(newBook.getTitle());
        newBookView.setAuthorName(newBook.getAuthorName());
        newBookView.setSummary(newBook.getSummary());
        newBookView.setCategory(newBook.getCategory());
        newBookView.setViewCount(0L); // 신규 등록 시 조회수 0
        newBookView.setIsbestseller(false); // 초기 베스트셀러 아님
        bookViewRepository.save(newBookView);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AccessRequestedAsSubscriber'"
    )
    public void wheneverAccessRequestedAsSubscriber_BookView(
        @Payload AccessRequestedAsSubscriber accessRequestedAsSubscriber
    ) {
        System.out.println(
            "\n\n##### listener BookView : " +
            accessRequestedAsSubscriber +
            "\n\n"
        );

        // Logic //

        // 1. 이벤트에서 BookId 추출
        Long bookId = accessRequestedAsSubscriber.getBookId();

        // 2. Book Aggregate 조회
        bookRepository.findById(bookId).ifPresent(book -> {
            // 3. Book Aggregate의 비즈니스 로직 호출 (조회수 증가)
            book.increaseViewCount();
            // increaseViewCount() 메서드 내부에서 BookViewed 이벤트를 발행하므로 별도 발행 코드 불필요

            // 4. 변경된 Book Aggregate 저장
            bookRepository.save(book); // Book 객체에 변경 사항 반영 후 DB에 저장
        });
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PointsDeducted'"
    )
    public void wheneverPointsDeducted_BookView(
        @Payload PointsDeducted pointsDeducted
    ) {
        System.out.println(
            "\n\n##### listener BookView : " + pointsDeducted + "\n\n"
        );

        // Logic //

        // 1. 이벤트에서 BookId 추출
        Long bookId = pointsDeducted.getBookId();

        // 2. Book Aggregate 조회
        bookRepository.findById(bookId).ifPresent(book -> {
            // 3. Book Aggregate의 비즈니스 로직 호출 (조회수 증가)
            book.increaseViewCount();
            // increaseViewCount() 메서드 내부에서 BookViewed 이벤트를 발행하므로 별도 발행 코드 불필요

            // 4. 변경된 Book Aggregate 저장
            bookRepository.save(book); // Book 객체에 변경 사항 반영 후 DB에 저장
        });
    }

    // BookViewed 이벤트를 받아 BookView(읽기 모델)를 업데이트하는 메서드
    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookViewed'" // Book에서 발행한 BookViewed 이벤트를 수신
    )
    public void wheneverBookViewed_UpdateBookView(@Payload BookViewed bookViewed) {

        System.out.println("\n\n##### Listener BookViewed (for Read Model Update) : " + bookViewed.toJson() + "\n\n");

        bookViewRepository.findById(bookViewed.getId()).ifPresentOrElse(
            bookView -> {
                // 기존 BookView가 있다면 업데이트
                bookView.updateFrom(bookViewed); // BookView의 updateFrom 메서드 호출
                bookViewRepository.save(bookView);
            },
            () -> {
                // BookView가 없다면 새로 생성 (첫 조회수 증가 시)
                BookView newBookView = new BookView();
                newBookView.updateFrom(bookViewed); // BookView의 updateFrom 메서드 호출
                bookViewRepository.save(newBookView);
            }
        );
    }
}
//>>> Clean Arch / Inbound Adaptor
