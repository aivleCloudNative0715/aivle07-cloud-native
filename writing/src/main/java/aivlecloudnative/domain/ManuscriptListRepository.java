package aivlecloudnative.domain;

import java.util.Optional;
import aivlecloudnative.domain.ManuscriptList; // ManuscriptList 엔티티 임포트
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository 임포트
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(
    collectionResourceRel = "manuscriptLists",
    path = "manuscriptLists"
)
public interface ManuscriptListRepository extends JpaRepository<ManuscriptList, Long> {

    List<ManuscriptList> findByAuthorId(String authorId);

    Optional<ManuscriptList> findByAuthorIdAndManuscriptId(String authorId, Long manuscriptId);

}