package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(
    collectionResourceRel = "publicationStatuses",
    path = "publicationStatuses"
)
public interface PublicationStatusRepository
    extends JpaRepository<PublicationStatus, Long> {   // <-- JpaRepository로 수정!
    List<PublicationStatus> findByManuscriptId(Long manuscriptId);
}
