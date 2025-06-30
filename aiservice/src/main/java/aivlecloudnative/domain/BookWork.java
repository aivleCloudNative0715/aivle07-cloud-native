package aivlecloudnative.domain;

import aivlecloudnative.AiserviceApplication; // ApplicationContext를 가져오기 위함
import jakarta.persistence.*; // javax➡️jakarta로 변경
import lombok.Data; // Lombok 어노테이션
import org.springframework.beans.BeanUtils; // 객체 복사를 위한 유틸리티

import java.time.LocalDate; // 필요하다면 유지
import java.util.Date; // 필요하다면 유지

@Entity
@Table(name = "BookWork_table")
@Data // Lombok: getter, setter, toString, equals, hashCode 자동 생성
// <<< DDD / Aggregate Root
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long manuscriptId;

    private String title;

    private String summary;

    private String keywords;

    private String authorName;

    private String coverImageUrl;

    private String ebookUrl;

    private String category;

    private Integer price;

    private String status;

    public static BookWorkRepository repository() {
        BookWorkRepository bookWorkRepository = AiserviceApplication.applicationContext.getBean(
                BookWorkRepository.class);
        return bookWorkRepository;
    }

    // --- 비즈니스 로직 메소드들 ---

    // requestNewBookPublication 메서드의 반환 타입을 BookWork로 변경했습니다.
    public static BookWork requestNewBookPublication(PublicationRequested event) {
        BookWork bookWork = new BookWork();

        bookWork.setManuscriptId(event.getManuscriptId());
        bookWork.setTitle(event.getTitle());
        bookWork.setSummary(event.getSummary());
        bookWork.setKeywords(event.getKeywords());
        bookWork.setAuthorName(event.getAuthorName());
        // 초기 상태를 PublicationInfoCreationRequested로 설정합니다.
        bookWork.setStatus("PublicationInfoCreationRequested");

        // 변경된 BookWork 객체를 저장합니다.
        repository().save(bookWork);

        // PublicationInfoCreationRequested 이벤트 발행
        PublicationInfoCreationRequested publicationInfoCreationRequested = new PublicationInfoCreationRequested();
        // 저장된 BookWork 객체의 속성을 이벤트 객체로 복사합니다.
        // 여기서 BeanUtils를 사용하려면 org.springframework.beans.BeanUtils를 임포트해야 합니다.
        BeanUtils.copyProperties(bookWork, publicationInfoCreationRequested);
        publicationInfoCreationRequested.publishAfterCommit();

        // 저장된 BookWork 객체를 반환합니다.
        return bookWork; // <--- 이 부분이 추가 및 수정되었습니다.
    }

    public void applyPublicationInfoAndAutoPublish(
            String coverImageUrl,
            String ebookUrl,
            String category,
            Integer price) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        this.setStatus("AutoPublished"); // 상태를 AutoPublished로 변경

        repository().save(this); // 변경된 BookWork 객체를 저장합니다.

        // AutoPublished 이벤트 발행
        AutoPublished autoPublished = new AutoPublished();
        BeanUtils.copyProperties(this, autoPublished);
        autoPublished.publishAfterCommit();
    }
}
// >>> DDD / Aggregate Root