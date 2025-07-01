package aivlecloudnative.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class JudgeAuthorCommand {
    private Long authorId;
    private String judgement;
}
