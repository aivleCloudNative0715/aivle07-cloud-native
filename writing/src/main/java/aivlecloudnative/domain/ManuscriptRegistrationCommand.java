package aivlecloudnative.domain;
import lombok.Data;
import java.time.LocalDateTime; // LocalDateTime만 사용하므로 이것만 남깁니다.
import java.util.*;
import jakarta.validation.constraints.NotBlank;

@Data
public class ManuscriptRegistrationCommand {

    @NotBlank(message = "저자 ID는 필수입니다.") 
    private String authorId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private String authorName;
    
    private String summary;
    private String keywords;
}