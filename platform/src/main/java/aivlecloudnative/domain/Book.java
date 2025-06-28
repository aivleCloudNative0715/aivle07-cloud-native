package aivlecloudnative.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import aivlecloudnative.PlatformApplication;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Book_table")
@Data
@NoArgsConstructor
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

    private Long viewCount;

    public static BookRepository repository() {
        BookRepository bookRepository = PlatformApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static Book registerNewBook(AutoPublished autoPublished) {
        //implement business logic here:
        
        Book book = new Book();
        // AutoPublished 이벤트에서 받은 데이터로 Book 객체 필드 초기화
        book.setTitle(autoPublished.getTitle());
        book.setAuthorName(autoPublished.getAuthorName());
        book.setSummary(autoPublished.getSummary());
        book.setCategory(autoPublished.getCategory());
        book.setCoverImageUrl(autoPublished.getCoverImageUrl());
        book.setEbookUrl(autoPublished.getEbookUrl());
        book.setPrice(autoPublished.getPrice());
        book.setViewCount(0L); // 신규 도서 등록 시 조회수는 0으로 초기화

        repository().save(book); // 새로 생성된 Book 객체 저장

        return book; // 생성된 Book 객체 반환
    }

    // bookView -> increaseViewCount로 이름 변경 (포인트,구독자 모두 여기로 접근)
    public void increaseViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 1L;
        } else {
            this.viewCount++;
        }

        // 조회수 증가 후 BookViewed 이벤트 발행
        BookViewed bookViewed = new BookViewed(this); // 현재 Book 객체(aggregate)를 인자로 전달
        bookViewed.publishAfterCommit(); // 트랜잭션 커밋 후 이벤트 발행 (AbstractEvent의 메서드)
    }


}
//>>> DDD / Aggregate Root
