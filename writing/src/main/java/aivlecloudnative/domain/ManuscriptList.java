package aivlecloudnative.domain;

import jakarta.persistence.Entity; // 변경: javax -> jakarta
import jakarta.persistence.Table;  // 변경: javax -> jakarta
import jakarta.persistence.Id;     // 변경: javax -> jakarta
import jakarta.persistence.Column;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

//<<< EDA / CQRS
@Entity
@Table(name = "ManuscriptList_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManuscriptList {

    @Id
    private Long manuscriptId;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String authorName;

    private String keywords;
    private String summary;

    private String status;
    private Long lastModifiedAt;
}