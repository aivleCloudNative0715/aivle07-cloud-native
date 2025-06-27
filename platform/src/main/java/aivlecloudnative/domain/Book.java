package aivlecloudnative.domain;

import aivlecloudnative.PlatformApplication;
import aivlecloudnative.domain.BookViewed;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Book_table")
@Data
//<<< DDD / Aggregate Root
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String authorName;

    private String summary;

    private String category;

    private String coverImageUrl;

    private String ebookUrl;

    private Integer price;

    private String viewCount;

    public static BookRepository repository() {
        BookRepository bookRepository = PlatformApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static void registerNewBook(AutoPublished autoPublished) {
        //implement business logic here:

        /** Example 1:  new item 
        Book book = new Book();
        repository().save(book);

        */

        /** Example 2:  finding and process
        

        repository().findById(autoPublished.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);


         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void bookView(
        AccessRequestedAsSubscriber accessRequestedAsSubscriber
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Book book = new Book();
        repository().save(book);

        BookViewed bookViewed = new BookViewed(book);
        bookViewed.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(accessRequestedAsSubscriber.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);

            BookViewed bookViewed = new BookViewed(book);
            bookViewed.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void bookView(PointsDeducted pointsDeducted) {
        //implement business logic here:

        /** Example 1:  new item 
        Book book = new Book();
        repository().save(book);

        BookViewed bookViewed = new BookViewed(book);
        bookViewed.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(pointsDeducted.get???()).ifPresent(book->{
            
            book // do something
            repository().save(book);

            BookViewed bookViewed = new BookViewed(book);
            bookViewed.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
