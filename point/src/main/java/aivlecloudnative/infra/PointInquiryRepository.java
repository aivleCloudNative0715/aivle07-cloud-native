package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "pointInquiries",
    path = "pointInquiries"
)
public interface PointInquiryRepository
    extends PagingAndSortingRepository<PointInquiry, Long> {
    List<PointInquiry> findByUserId(Long userId);
}
