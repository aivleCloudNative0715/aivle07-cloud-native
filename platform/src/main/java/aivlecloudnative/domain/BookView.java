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
//<<< DDD / Aggregate Root
public class BookView {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private String title;

    private String authorName;

    private String summary;

    private String category;

    private Integer viewCount;

    private Boolean isbestseller;

    public static BookViewRepository repository() {
        BookViewRepository bookViewRepository = PlatformApplication.applicationContext.getBean(
            BookViewRepository.class
        );
        return bookViewRepository;
    }
}
//>>> DDD / Aggregate Root
