package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "publicationStatuses",
    path = "publicationStatuses"
)
public interface PublicationStatusRepository
    extends PagingAndSortingRepository<PublicationStatus, Long> {
    List<PublicationStatus> findByManuscriptId(Long manuscriptId);
}
