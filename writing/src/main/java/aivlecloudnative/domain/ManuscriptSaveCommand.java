package aivlecloudnative.domain;

import java.util.*;
import java.time.LocalDateTime; // LocalDateTime을 사용하므로 추가

import lombok.Data;

@Data
public class ManuscriptSaveCommand {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime lastModifiedAt; // Date -> LocalDateTime으로 변경
    private String summary;
    private String keywords;
}