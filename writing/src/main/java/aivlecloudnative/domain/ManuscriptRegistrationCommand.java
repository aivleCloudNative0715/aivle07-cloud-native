package aivlecloudnative.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ManuscriptRegistrationCommand {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private Date lastModifiedAt;
    private String summary;
    private String keywords;
}
