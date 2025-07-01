package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookViewRepository extends JpaRepository<BookView, Long> {

    // 특정 사용자의 특정 도서 열람 기록을 조회하는 메서드
    Optional<BookView> findByUserIdAndBookId(String userId, Long bookId);

    // 특정 사용자의 모든 도서 열람 기록을 조회하는 메서드
    List<BookView> findByUserId(String userId);
}
