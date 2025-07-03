package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // AbstractEvent의 필드까지 고려하여 equals와 hashCode 생성
public class AutoPublished extends AbstractEvent {
    // AI 서비스에서 오는 데이터
    private Long id; // 책 작업 관리 관련 ID (이벤트 식별용)
    private String title;
    private Long manuscriptId; // 원고 ID
    private String summary;
    private String keywords;
    private String authorName;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Double price; // 가격은 일반적으로 Double 또는 BigDecimal 사용
    private String status; // 출간 상태 (예: "PUBLISHED")
    private String authorId; // 저자의 email

    // 생성자
    public AutoPublished(
            Long id, String title, Long manuscriptId, String summary,
            String keywords, String authorName, String coverImageUrl,
            String ebookUrl, String category, Double price, String status,
            String authorId) {
        super(); // AbstractEvent의 생성자 호출
        this.id = id;
        this.title = title;
        this.manuscriptId = manuscriptId;
        this.summary = summary;
        this.keywords = keywords;
        this.authorName = authorName;
        this.coverImageUrl = coverImageUrl;
        this.ebookUrl = ebookUrl;
        this.category = category;
        this.price = price;
        this.status = status;
        this.authorId = authorId;
    }
}
