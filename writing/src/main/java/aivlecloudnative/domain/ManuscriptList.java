package aivlecloudnative.domain;

import jakarta.persistence.Entity; // 변경: javax -> jakarta
import jakarta.persistence.Table;  // 변경: javax -> jakarta
import jakarta.persistence.Id;     // 변경: javax -> jakarta
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Column; 

import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "ManuscriptList_table")
@Data
public class ManuscriptList {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO) 
    private Long id;

    private Long manuscriptId;
    private String manuscriptTitle;
    private String manuscriptContent;
    private String manuscriptStatus;
    private String lastModifiedAt; 
    private String authorId; // 오타 (autor -> author)

}