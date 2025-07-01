package aivlecloudnative.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "publicationStatuses",
    path = "publicationStatuses"
)
public interface PublicationStatusRepository
    extends JpaRepository<PublicationStatus, Long> {   // <<== 여기!
    List<PublicationStatus> findByManuscriptId(Long manuscriptId);
}
