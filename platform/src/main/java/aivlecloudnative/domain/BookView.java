package aivlecloudnative.domain;

import aivlecloudnative.PlatformApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BookView_table")
@Data
@NoArgsConstructor 
//<<< DDD / Aggregate Root
public class BookView {

    @Id
    private Long bookId;

    private String title;

    private String authorName;

    private String summary;

    private String category;

    private Long viewCount;

    private Boolean isbestseller;

    public static BookViewRepository repository() {
        BookViewRepository bookViewRepository = PlatformApplication.applicationContext.getBean(
            BookViewRepository.class
        );
        return bookViewRepository;
    }

    // BookViewed 이벤트를 받아서 BookView를 업데이트하는 헬퍼 메서드
    public void updateFrom(BookViewed event) {
        this.bookId = event.getId(); // BookViewed 이벤트의 id는 Book의 id
        this.title = event.getTitle();
        this.viewCount = event.getViewCount();
        
        // 조회수 3회 이상이면 베스트셀러로 간주
        if (event.getViewCount() >= 3) {
            this.isbestseller = true;
        } else {
            this.isbestseller = false;
        }
    }
}
//>>> DDD / Aggregate Root
