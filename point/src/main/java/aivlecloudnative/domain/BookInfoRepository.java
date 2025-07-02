package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookInfoRepository extends JpaRepository<BookInfo, Long> {
    Optional<BookInfo> findByBookId(String bookId); // bookId로 도서 정보 조회
}