package aivlecloudnative.domain;

import jakarta.persistence.Entity; // 변경: javax -> jakarta
import jakarta.persistence.Table;  // 변경: javax -> jakarta
import jakarta.persistence.Id;     // 변경: javax -> jakarta
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Column; 
import java.time.LocalDateTime;

import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "ManuscriptList_table")
@Data
public class ManuscriptList {

    @Id
    @GeneratedValue
    private Long manuscriptId;
    
    private String manuscriptTitle;
    private String manuscriptContent;
    private String manuscriptStatus;
    private LocalDateTime lastModifiedAt;
    private String authorId;
}