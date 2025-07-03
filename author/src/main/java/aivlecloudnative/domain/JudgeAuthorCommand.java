package aivlecloudnative.domain;

import lombok.Data;

@Data
public class JudgeAuthorCommand {
    private Long userId; // 심사할 작가(사용자)의 ID
    private Boolean isApproved; // true면 승인, false면 거부
}