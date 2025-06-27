package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "bookViews", path = "bookViews")
public interface BookViewRepository
    extends PagingAndSortingRepository<BookView, Long> {}
