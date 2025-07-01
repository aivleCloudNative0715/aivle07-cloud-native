package aivlecloudnative.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ApplyAuthorCommand {
    private String authorName;
    private String authorEmail;
    private String portfolio;
}
