package aivlecloudnative.domain;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "book")
@Data
//<<< DDD / Aggregate Root
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 ID 자동 할당
    private Long id; // Book의 고유 ID
    private String title;
    private String summary;
    private String authorName;
    private String category;
    private String coverImageUrl;
    private String ebookUrl;
    private Double price;
    private Long viewCount; // 신규 등록 시 0으로 초기화

    public Book() {
        this.viewCount = 0L; // 초기화
    }

}
//>>> DDD / Aggregate Root
