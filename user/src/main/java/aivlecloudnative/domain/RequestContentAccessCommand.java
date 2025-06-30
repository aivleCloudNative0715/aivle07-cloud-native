package aivlecloudnative.domain;

import lombok.Data;

@Data
public class RequestContentAccessCommand {

    private Long userId;
    private Long bookId;
}
