package aivlecloudnative.domain;

import aivlecloudnative.WritingApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Manuscript_table")
@Data
//<<< DDD / Aggregate Root
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String authorId;

    private String title;

    private String content;

    private String status;

    private Date lastModifiedAt;

    private String summary;

    private String keywords;

    public static ManuscriptRepository repository() {
        ManuscriptRepository manuscriptRepository = WritingApplication.applicationContext.getBean(
            ManuscriptRepository.class
        );
        return manuscriptRepository;
    }

    //<<< Clean Arch / Port Method
    public void publicationRequest(
        PublicationRequestCommand publicationRequestCommand
    ) {
        //implement business logic here:

        PublicationRequested publicationRequested = new PublicationRequested(
            this
        );
        publicationRequested.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void manuscriptRegistration(
        ManuscriptRegistrationCommand manuscriptRegistrationCommand
    ) {
        //implement business logic here:

        ManuscriptRegisterd manuscriptRegisterd = new ManuscriptRegisterd(this);
        manuscriptRegisterd.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void manuscriptSave(ManuscriptSaveCommand manuscriptSaveCommand) {
        //implement business logic here:

        ManuscriptSaved manuscriptSaved = new ManuscriptSaved(this);
        manuscriptSaved.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
