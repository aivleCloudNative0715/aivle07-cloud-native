package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "manuscriptLists",
    path = "manuscriptLists"
)
public interface ManuscriptListRepository
    extends PagingAndSortingRepository<ManuscriptList, Long> {
    List<ManuscriptList> findByAutorId(String autorId);
}
