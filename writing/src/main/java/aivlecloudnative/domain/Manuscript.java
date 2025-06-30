package aivlecloudnative.domain;

import aivlecloudnative.WritingApplication;
import aivlecloudnative.infra.AbstractEvent;

// 이벤트 생성 DTO 임포트
import aivlecloudnative.domain.ManuscriptRegistrationCommand;
import aivlecloudnative.domain.ManuscriptSaveCommand;
import aivlecloudnative.domain.PublicationRequestCommand;

// 발행할 이벤트 DTO 임포트
import aivlecloudnative.domain.ManuscriptRegisterd;
import aivlecloudnative.domain.ManuscriptSaved;
import aivlecloudnative.domain.PublicationRequested;

import com.fasterxml.jackson.databind.ObjectMapper; 
import jakarta.persistence.Entity;     
import jakarta.persistence.Id;      
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Table;      

import jakarta.persistence.PostPersist; // 엔티티가 DB에 영속화된 후 호출될 콜백
import jakarta.persistence.PostUpdate; // 엔티티가 DB에서 업데이트된 후 호출될 콜백


import lombok.Data; 

import java.time.LocalDateTime; // 변경: java.util.Date 대신 사용 

@Entity
@Table(name = "Manuscript_table")
@Data
//<<< DDD / Aggregate Root
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorId;

    private String title;

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
        // 원고 등록 이벤트발행
        ManuscriptRegisterd manuscriptRegisterd = new ManuscriptRegisterd(this); // 현재 Manuscript 객체로 이벤트 생성
        manuscriptRegisterd.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        // 엔티티의 'status' 필드를 기준으로 어떤 이벤트를 발행할지
        if ("SAVED".equals(this.status)) { 
            ManuscriptSaved manuscriptSaved = new ManuscriptSaved(this);
            manuscriptSaved.publishAfterCommit();
        } else if ("PUBLICATION_REQUESTED".equals(this.status)) {
            PublicationRequested publicationRequested = new PublicationRequested(this);
            publicationRequested.publishAfterCommit();
        }
        // 다른 상태 변경에 대한 이벤트 발행도 여기에 추가 가능 (예: "APPROVED", "REJECTED" 등)
    }

}
//>>> DDD / Aggregate Root
