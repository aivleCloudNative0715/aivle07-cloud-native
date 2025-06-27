package aivlecloudnative.domain;

import aivlecloudnative.AiserviceApplication;
import aivlecloudnative.domain.AutoPublished;
import aivlecloudnative.domain.PublicationInfoCreationRequested;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BookWork_table")
@Data
//<<< DDD / Aggregate Root
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long manuscriptId;

    private String title;

    private String summary;

    private String keywords;

    private String coverImageUrl;

    private String ebookUrl;

    private String category;

    private Integer price;

    private String status;

    public static BookWorkRepository repository() {
        BookWorkRepository bookWorkRepository = AiserviceApplication.applicationContext.getBean(
            BookWorkRepository.class
        );
        return bookWorkRepository;
    }

    //<<< Clean Arch / Port Method
    public static void autoPublicationRequest(
        PublicationRequested publicationRequested
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        BookWork bookWork = new BookWork();
        repository().save(bookWork);

        PublicationInfoCreationRequested publicationInfoCreationRequested = new PublicationInfoCreationRequested(bookWork);
        publicationInfoCreationRequested.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(publicationRequested.get???()).ifPresent(bookWork->{
            
            bookWork // do something
            repository().save(bookWork);

            PublicationInfoCreationRequested publicationInfoCreationRequested = new PublicationInfoCreationRequested(bookWork);
            publicationInfoCreationRequested.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
