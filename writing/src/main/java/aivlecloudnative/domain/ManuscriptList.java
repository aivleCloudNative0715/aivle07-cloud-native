package aivlecloudnative.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
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
    private String autorId;
}
