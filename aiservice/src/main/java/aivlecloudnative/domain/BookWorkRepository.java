package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookWorkRepository extends JpaRepository<BookWork, Long> {
        // JpaRepository를 상속받으면 findById, save, findAll 등 기본 CRUD 메서드가 자동으로 제공
}