package aivlecloudnative.domain;

import java.time.LocalDateTime;
import java.util.*;
import lombok.Data;

@Data
public class PublicationRequestCommand {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime lastModifiedAt;
    private String summary;
    private String keywords;
}
