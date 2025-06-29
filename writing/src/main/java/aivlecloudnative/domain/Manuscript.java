package aivlecloudnative.domain;

import aivlecloudnative.WritingApplication;

import com.fasterxml.jackson.databind.ObjectMapper; 
import jakarta.persistence.Entity;     
import jakarta.persistence.Id;      
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Table;      


import lombok.Data; 

import java.time.LocalDateTime; // 변경: java.util.Date 대신 사용 권장

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

    private LocalDateTime lastModifiedAt; // Date -> LocalDateTime으로 변경

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
