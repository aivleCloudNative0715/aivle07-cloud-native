package aivlecloudnative.infra; // 파일을 생성한 패키지 경로에 맞춥니다.

import lombok.Data; // Lombok 어노테이션을 사용하므로 lombok이 pom.xml에 추가되어 있어야 합니다.

@Data // getter, setter, toString, equals, hashCode를 자동으로 생성해줍니다.
public class BookWorkRequestDto {
    private Long manuscriptId;
    private String title;
    private String summary;
    private String keywords;
    private String authorName;
    private String status;
    // id, coverImageUrl, ebookUrl, category, price 등은
    // 클라이언트(curl)가 POST 요청 시 보내지 않는 정보이므로 이 DTO에는 포함하지 않습니다.
}