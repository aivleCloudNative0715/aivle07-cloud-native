package aivlecloudnative.domain;

import aivlecloudnative.WritingApplication;
import aivlecloudnative.infra.AbstractEvent;
// import aivlecloudnative.infra.EventPublisher; // <-- 제거: EventPublisher는 더 이상 사용하지 않습니다.

import org.springframework.beans.BeanUtils; // 객체 속성 복사를 위해 필요
// import org.springframework.beans.factory.annotation.Autowired; // <-- 제거: EventPublisher 주입이 필요 없어졌습니다.

// 아웃박스 패턴을 위한 EventOutboxSaver 임포트
import aivlecloudnative.domain.EventOutboxSaver; // <-- 추가

// 발행할 이벤트 DTO 임포트
import aivlecloudnative.domain.ManuscriptRegistered;
import aivlecloudnative.domain.ManuscriptSaved;
import aivlecloudnative.domain.PublicationRequested;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

//<<< DDD / Aggregate Root
@Entity
@Table(name = "Manuscript_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String status;
    private LocalDateTime lastModifiedAt; // Date -> LocalDateTime으로 변경
    private String summary;
    private String keywords;

    public static ManuscriptRepository repository() {
        ManuscriptRepository manuscriptRepository = WritingApplication.applicationContext.getBean(
            ManuscriptRepository.class
        );
        return manuscriptRepository;
    }

    // EventOutboxSaver에 접근하기 위한 헬퍼 메서드 추가
    public static EventOutboxSaver eventOutboxSaver() {
        return WritingApplication.applicationContext.getBean(EventOutboxSaver.class);
    }

    // -- 비지니스 로직 및 이벤트 발행 부분 --

    // 원고 상태를 'SAVED'로 변경하는 비즈니스 메서드
    public void changeStatusToSaved() {

        this.setStatus("SAVED");
        this.setLastModifiedAt(LocalDateTime.now());

    }

    // 원고 상태를 'PUBLICATION_REQUESTED'로 변경하는 비즈니스 메서드
    public void requestPublication() {
        // 출간 요청이 가능한 상태인지 검증
        if (!"SAVED".equals(this.status) && !"REGISTERED".equals(this.status)) {
            throw new IllegalStateException("Manuscript must be in SAVED or REGISTERED status to request publication.");
        }
        this.setStatus("PUBLICATION_REQUESTED");
        this.setLastModifiedAt(LocalDateTime.now());
    }

    // 초기 등록 시 원고 객체를 생성하는 팩토리 메서드
    public static Manuscript createNew(String authorId, String title, String content, String summary, String keywords) {

        Manuscript manuscript = new Manuscript();
        manuscript.setAuthorId(authorId);
        manuscript.setTitle(title);
        manuscript.setContent(content);
        // 초기 상태 설정
        manuscript.setStatus("REGISTERED");
        manuscript.setLastModifiedAt(LocalDateTime.now());
        manuscript.setSummary(summary);
        manuscript.setKeywords(keywords);
        return manuscript;
    }

    @PostPersist
    public void onPostPersist() {
        // 원고 등록 이벤트발행 -> EventOutboxSaver를 통해 저장
        ManuscriptRegistered manuscriptRegistered = new ManuscriptRegistered(this);
        eventOutboxSaver().save(manuscriptRegistered);
    }

    @PostUpdate
    public void onPostUpdate() {
        // 엔티티의 'status' 필드를 기준으로 어떤 이벤트를 발행할지
        if ("SAVED".equals(this.status)) {
            ManuscriptSaved manuscriptSaved = new ManuscriptSaved(this);
            eventOutboxSaver().save(manuscriptSaved);
        } else if ("PUBLICATION_REQUESTED".equals(this.status)) {
            PublicationRequested publicationRequested = new PublicationRequested(this);
            eventOutboxSaver().save(publicationRequested);
        }
    }

}
//>>> DDD / Aggregate Root