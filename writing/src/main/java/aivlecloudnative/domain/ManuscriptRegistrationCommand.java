package aivlecloudnative.domain;
import lombok.Data;
import java.time.LocalDateTime; // LocalDateTime만 사용하므로 이것만 남깁니다.
import java.util.*;

@Data
public class ManuscriptRegistrationCommand {

    private Long id;
    private String authorId;
    private String title;
    private String content;
    private String status;
    private LocalDateTime lastModifiedAt;
    private String summary;
    private String keywords;
}
