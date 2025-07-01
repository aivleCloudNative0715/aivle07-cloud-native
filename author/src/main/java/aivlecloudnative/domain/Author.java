package aivlecloudnative.domain;

import aivlecloudnative.AuthorApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Author_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public Author(String authorName, String authorEmail, String portfolio) {
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.portfolio = portfolio;
        this.isApproved = null; // 지원 상태
    }

    public static AuthorRepository repository() {
        return AuthorApplication.applicationContext.getBean(AuthorRepository.class);
    }

    public void apply() {
        this.isApproved = null;
    }

    public void accept() {
        this.isApproved = true;
    }

    public void reject() {
        this.isApproved = false;
    }

    public void applyAuthor() {
        AuthorApplied authorApplied = new AuthorApplied(this);
        authorApplied.publishAfterCommit();
    }

    public void judgeAuthor() {
        AuthorRejected authorRejected = new AuthorRejected(this);
        authorRejected.publishAfterCommit();
        AuthorAccepted authorAccepted = new AuthorAccepted(this);
        authorAccepted.publishAfterCommit();
    }
}
