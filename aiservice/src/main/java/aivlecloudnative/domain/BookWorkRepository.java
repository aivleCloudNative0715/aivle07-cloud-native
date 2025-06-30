package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.PagingAndSortingRepository; // 만약 기존에 이것이었다면 JpaRepository로 변경

// BookWork 엔티티와 ID 타입(Long)을 지정하여 JpaRepository를 상속받습니다.
public interface BookWorkRepository extends JpaRepository<BookWork, Long> {
        // JpaRepository를 상속받으면 findById, save, findAll 등 기본 CRUD 메서드가 자동으로 제공됩니다.
        // 따라서 여기에는 별도로 findById 메서드를 정의할 필요가 없습니다.
}