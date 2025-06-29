package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent; 
import java.util.Date; 
import java.util.List; 

import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "manuscripts",
    path = "manuscripts"
)
public interface ManuscriptRepository
    extends PagingAndSortingRepository<Manuscript, Long> {
        // 여기에 Query 메서드나, Pageable을 사용하는 메서드 등이 추가될 수 있습니다.
        // 예를 들어:
        // Page<Manuscript> findByAuthorId(String authorId, Pageable pageable);
        // List<Manuscript> findByStatus(String status);
}