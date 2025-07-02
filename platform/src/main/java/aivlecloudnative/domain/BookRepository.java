package aivlecloudnative.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // isBestseller가 true인 도서를 조회하는 메서드
    List<Book> findByIsBestseller(Boolean isBestseller);

    // isBestseller가 true인 도서를 페이징하여 조회하는 메서드 (정렬 포함 가능)
    Page<Book> findByIsBestseller(Boolean isBestseller, Pageable pageable);

    // authorId로 도서를 조회하는 메서드
    List<Book> findByAuthorId(String authorId);

    // authorId로 도서를 페이징하여 조회하는 메서드
    Page<Book> findByAuthorId(String authorId, Pageable pageable);
}