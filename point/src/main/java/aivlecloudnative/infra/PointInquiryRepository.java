package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.List;
import java.util.Optional;
// import org.springframework.data.repository.PagingAndSortingRepository; // <-- 이 임포트를 제거 또는 주석 처리
import org.springframework.data.jpa.repository.JpaRepository; // <-- 이 임포트를 추가

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository 
@RepositoryRestResource(
    collectionResourceRel = "pointInquiries",
    path = "pointInquiries"
)
public interface PointInquiryRepository
    // extends PagingAndSortingRepository<PointInquiry, Long> { // <-- 이 줄을
    extends JpaRepository<PointInquiry, Long> { // <-- 이 줄로 변경합니다.
    List<PointInquiry> findByUserId(Long userId);
}