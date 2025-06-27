package aivlecloudnative.domain;

import aivlecloudnative.AuthorApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Author_table")
@Data
//<<< DDD / Aggregate Root
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String authorEmail;

    private String authorName;

    private String bio;

    private String representativeWork;

    private String portfolio;

    private Boolean isApproved;

    public static AuthorRepository repository() {
        AuthorRepository authorRepository = AuthorApplication.applicationContext.getBean(
            AuthorRepository.class
        );
        return authorRepository;
    }

    //<<< Clean Arch / Port Method
    public void applyAuthor() {
        //implement business logic here:

        AuthorApplied authorApplied = new AuthorApplied(this);
        authorApplied.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void judgeAuthor() {
        //implement business logic here:

        AuthorRejected authorRejected = new AuthorRejected(this);
        authorRejected.publishAfterCommit();
        AuthorAccepted authorAccepted = new AuthorAccepted(this);
        authorAccepted.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
