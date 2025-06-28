package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "bookViews", path = "bookViews")
public interface BookViewRepository
    extends PagingAndSortingRepository<BookView, Long> {
        // 특정 카테고리의 BookView를 조회
        List<BookView> findByCategory(String category);

        // 베스트셀러 목록을 조회하는 메서드
        Page<BookView> findByIsbestsellerTrue(Pageable pageable);

        // 조회수 기준으로 정렬하여 상위 N개 조회하는 메서드
        // 페이징 지원
        Page<BookView> findByOrderByViewCountDesc(Pageable pageable);
    }
