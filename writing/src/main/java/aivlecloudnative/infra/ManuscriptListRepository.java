package aivlecloudnative.infra;

import java.util.Optional;
import aivlecloudnative.domain.ManuscriptList; // ManuscriptList 엔티티 임포트
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository 임포트
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(
    collectionResourceRel = "manuscriptLists",
    path = "manuscriptLists"
)
public interface ManuscriptListRepository
    extends JpaRepository<ManuscriptList, Long> { // JpaRepository로 변경

    List<ManuscriptList> findByAuthorId(String authorId);

    // Manuscript ID로 ManuscriptList를 찾는 메서드 추가
    Optional<ManuscriptList> findByManuscriptId(Long manuscriptId);

}