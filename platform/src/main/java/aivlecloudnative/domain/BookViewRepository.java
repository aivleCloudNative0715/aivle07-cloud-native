package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "bookViews", path = "bookViews")
public interface BookViewRepository
    extends PagingAndSortingRepository<BookView, Long> {
        // 특정 카테고리의 BookView를 조회
        List<BookView> findByCategory(String category);

        // 베스트셀러 목록을 조회하는 메서드
        List<BookView> findByIsbestsellerTrue(Pageable pageable);

        // 조회수 기준으로 상위 N개 조회 (RepositoryRestResource가 기본으로 제공하지 않는 커스텀 쿼리)
        List<BookView> findTop10ByOrderByViewCountDesc();
    }
