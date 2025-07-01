package aivlecloudnative.infra;

import lombok.Data;

@Data
public class BookWorkRequestDto {
    // Manuscript ID를 일관되게 'ManuscriptId'로 명명
    private Long ManuscriptId;
    private String title;
    private String content; // ✨ PublicationRequested 및 BookWork 엔티티에 맞춰 'content' 필드 추가
    private String summary;
    private String keywords;
    private String authorName;
    // status 필드는 API 요청 시 클라이언트가 보내는 정보라기보다는
    // 서버 비즈니스 로직에 의해 결정되는 것이 일반적이므로 DTO에서 제거하거나 선택적으로 사용합니다.
    // 여기서는 일단 제거합니다.
}