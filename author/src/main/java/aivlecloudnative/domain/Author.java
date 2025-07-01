package aivlecloudnative.domain;

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

    public void apply() {
        this.isApproved = null;
    }

    public void accept() {
        this.isApproved = true;
    }

    public void reject() {
        this.isApproved = false;
    }

    // 이벤트 발행 관련 메서드는 서비스 계층에서 처리하는 게 베스트!
}
