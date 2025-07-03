package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "authors", path = "authors")
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // userId로 Author를 조회하는 메서드 (AuthorService에서 사용)
    Optional<Author> findByUserId(Long userId);

    // 작가 신청 목록 조회 (isApproved가 null인 Author)
    List<Author> findByAppliedAtIsNotNullAndAcceptedAtIsNullAndRejectedAtIsNull();

    // 승인된 작가 목록 조회 (acceptedAt이 null이 아닌 Author)
    List<Author> findByAcceptedAtIsNotNull();

    // 거부된 작가 목록 조회 (rejectedAt이 null이 아닌 Author)
    List<Author> findByRejectedAtIsNotNull();

    // authorId (이메일)로 Author를 조회하는 메서드 (필요시 사용)
    Optional<Author> findByAuthorId(String authorId);
}