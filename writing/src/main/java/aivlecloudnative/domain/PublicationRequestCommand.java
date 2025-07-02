package aivlecloudnative.domain;

import java.time.LocalDateTime;
import java.util.*;
import lombok.Data;

@Data
public class PublicationRequestCommand {

    private String authorId;
    private Long manuscriptId;

}