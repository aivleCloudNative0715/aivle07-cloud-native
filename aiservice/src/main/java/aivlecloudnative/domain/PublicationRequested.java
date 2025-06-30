package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent; // 이 경로가 맞다면 유지
import java.util.*;
import lombok.*;

@Data
@ToString
@NoArgsConstructor // ✨ 이벤트를 메시지 브로커에서 받아 역직렬화하려면 기본 생성자가 필수적입니다.
public class PublicationRequested extends AbstractEvent {

    // ✨ writing 서비스의 PublicationRequested 스펙과 정확히 일치시켜야 합니다.
    private Long manuscriptIdId; // writing의 스펙에 맞춰 "Id" 두 번 붙여서 일치 (아니라면 manuscriptId)
    private String title;
    private String content;
    private String summary;
    private String keywords;
    private String authorName;
}