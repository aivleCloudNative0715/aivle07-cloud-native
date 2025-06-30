package aivlecloudnative.domain;

import java.util.*;
import java.time.LocalDateTime; // LocalDateTime을 사용하므로 추가

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ManuscriptSaveCommand {
    @NotBlank(message = "요청하는 작가 ID는 필수입니다.")
    private String authorId; // 혹은 requestingAuthorId로 이름 변경 고려

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private String summary;
    private String keywords;
}