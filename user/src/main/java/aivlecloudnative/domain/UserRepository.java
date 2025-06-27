package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    @Query(
        value = "select user " +
        "from User user " +
        "where(:id is null or user.id = :id)"
    )
    User viewSubscriptionHistory(Integer id);

    @Query(
        value = "select user " +
        "from User user " +
        "where(:id is null or user.id = :id)"
    )
    User viewContectHistory(Integer id);
}
