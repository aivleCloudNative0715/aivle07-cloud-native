package aivlecloudnative.domain;

import lombok.Data;

@Data
public class ApplyAuthorCommand {
    // private Long userId; // JWT에서 userId를 항상 추출하므로, 요청 바디에서는 제거
    private String authorEmail; // 이메일 형식 (User 서버의 email과 동일)
    private String authorName; // 작가 이름
    private String bio; // 작가 소개
    private String representativeWork; // 대표작
    private String portfolio; // 포트폴리오 URL 또는 내용
}