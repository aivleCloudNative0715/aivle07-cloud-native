package aivlecloudnative.infra;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookWorkRequestDto {

    private Long manuscriptId;
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorId;
    private String authorName;
}