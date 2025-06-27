package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "bookWorks", path = "bookWorks")
public interface BookWorkRepository
    extends PagingAndSortingRepository<BookWork, Long> {}
