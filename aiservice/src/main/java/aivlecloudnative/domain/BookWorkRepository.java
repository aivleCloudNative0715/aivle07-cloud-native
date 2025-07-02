package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookWorkRepository extends JpaRepository<BookWork, Long> {
        List<BookWork> findByAuthorId(String authorId);
        Optional<BookWork> findByManuscriptId(Long manuscriptId);
}