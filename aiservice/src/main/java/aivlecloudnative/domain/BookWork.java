package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import jakarta.persistence.*; // <-- 여기를 javax에서 jakarta로 변경
import java.time.LocalDateTime;

@Entity
@Table(name = "book_work_table")
public class BookWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long manuscriptIdId; // 원고 ID
    private String title;
    private String summary;
    private String keywords;
    private String authorName;
    @Lob // 큰 텍스트 저장을 위해 @Lob 추가
    private String content;
    private String coverImageUrl;
    private String ebookUrl;
    private String category;
    private Integer price; // Integer로 유지
    private String status; // 상태 필드
    private LocalDateTime createdDate;

    // 이전의 requestNewBookPublication 메서드 시그니처만 남기고 내부 로직 제거
    public static BookWork requestNewBookPublication(PublicationRequested publicationRequested) {
        // 이 메서드는 이제 사용되지 않거나, BookWork 객체를 생성하는 단순 역할만 수행해야 합니다.
        // 여기서는 PublicationRequested 이벤트를 기반으로 BookWork 객체를 생성하는 로직만 남깁니다.
        BookWork bookWork = new BookWork();
        bookWork.setManuscriptIdId(publicationRequested.getManuscriptIdId());
        bookWork.setTitle(publicationRequested.getTitle());
        bookWork.setSummary(publicationRequested.getSummary());
        bookWork.setKeywords(publicationRequested.getKeywords());
        bookWork.setAuthorName(publicationRequested.getAuthorName());
        bookWork.setContent(publicationRequested.getContent()); // 필요하다면 content도 설정
        bookWork.setStatus("PublicationInfoCreationRequested"); // 초기 상태 설정
        bookWork.setCreatedDate(LocalDateTime.now());
        // 이벤트 발행 로직은 여기서 제거
        return bookWork;
    }

    // AI 응답으로 정보 업데이트 및 최종 이벤트 발행
    public void applyPublicationInfoAndAutoPublish(String coverImageUrl, String ebookUrl, String category,
            Integer price, String aiGeneratedSummary) {
        this.setCoverImageUrl(coverImageUrl);
        this.setEbookUrl(ebookUrl);
        this.setCategory(category);
        this.setPrice(price);
        this.setStatus("AutoPublished"); // 최종 상태
    }

    // Lombok을 사용하지 않는다면 아래 getter/setter 필요
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManuscriptIdId() {
        return manuscriptIdId;
    }

    public void setManuscriptIdId(Long manuscriptIdId) {
        this.manuscriptIdId = manuscriptIdId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getEbookUrl() {
        return ebookUrl;
    }

    public void setEbookUrl(String ebookUrl) {
        this.ebookUrl = ebookUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}