package aivlecloudnative.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "manuscripts", path = "manuscripts")
public interface ManuscriptRepository extends JpaRepository<Manuscript, Long> {

}