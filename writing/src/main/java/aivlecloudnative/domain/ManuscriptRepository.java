package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent; 
import java.util.Date; 
import java.util.List; 

import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "manuscripts", path = "manuscripts")
public interface ManuscriptRepository extends JpaRepository<Manuscript, Long> {
}