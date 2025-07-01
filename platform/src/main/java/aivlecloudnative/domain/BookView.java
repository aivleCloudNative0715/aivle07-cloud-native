package aivlecloudnative.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_view", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "bookId"})
})
@Data
//<<< DDD / Aggregate Root
public class BookView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BookView의 고유 ID

    private Long bookId; // 어떤 책을 열람했는지 (Book 엔티티의 ID)
    private String userId; // 누가 열람했는지 (사용자 ID)
    private Long viewCount; // 특정 유저가 이 책을 열람한 횟수

    private LocalDateTime firstViewedAt; // 첫 열람 시간
    private LocalDateTime lastViewedAt; // 마지막 열람 시간

    public BookView() {
    }
}
//>>> DDD / Aggregate Root
