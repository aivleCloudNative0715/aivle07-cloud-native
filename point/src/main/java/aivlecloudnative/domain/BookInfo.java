package aivlecloudnative.domain;

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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동 생성되는 ID
    private Long id; // BookInfo 고유 ID
    
    private String bookId; // 실제 도서 서비스의 도서 ID
    private Long price;    // 이 도서를 열람하는 데 필요한 포인트 가격
}