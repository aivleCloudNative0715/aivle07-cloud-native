package aivlecloudnative.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.NoArgsConstructor;
import lombok.ToString; // ToString 어노테이션을 명시적으로 import (선택 사항)
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book_work_table")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "content" })
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long manuscriptId;
    private String title;

    @Lob
    private String content;
    private String summary;

    private String keywords;

    @Column(length = 1000)
    private String coverImageUrl;

    @Column(length = 1000)
    private String ebookUrl;
    private String category;
    private Integer price;
    private String status;
    private String authorId;
    private String authorName;

    //초기 출간등록 정보 저장을 위한 팩토리 매서드
    public static BookWork createRequestedBookWork(Long manuscriptId, String title, String content, String summary, String authorName, String keywords, String authorId) {
        return BookWork.builder()
                .manuscriptId(manuscriptId)
                .title(title)
                .content(content)
                .summary(summary)
                .authorName(authorName)
                .keywords(keywords)
                .authorId(authorId)
                .status("PUBLICATION_REQUESTED")
                .build();
    }

    // AI 응답으로 정보 업데이트 및 최종 이벤트 발행
    public void completeAiProcessing(String coverImageUrl, String ebookUrl, String category, Integer price) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        this.setStatus("AUTO_PUBLISHED");
    }

}