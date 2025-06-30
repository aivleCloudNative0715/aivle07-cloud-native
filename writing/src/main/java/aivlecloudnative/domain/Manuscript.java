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
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @PostPersist
    public void onPostPersist() {
        // 원고 등록 이벤트(ManuscriptRegisterd) 발행
        ManuscriptRegisterd manuscriptRegisterd = new ManuscriptRegisterd(this); // 현재 Manuscript 객체로 이벤트 생성
        manuscriptRegisterd.publishAfterCommit(); 
    }

    @PostUpdate
    public void onPostUpdate() {
        // 엔티티의 'status' 필드를 기준으로 어떤 이벤트를 발행할지

        // 1. 'SAVED' 상태로 변경된 경우 -> 원고저장됨이벤트 발행
        if ("SAVED".equals(this.status)) {
            ManuscriptSaved manuscriptSaved = new ManuscriptSaved(this);
            manuscriptSaved.publishAfterCommit();
        }
        // 2. 'PUBLICATION_REQUESTED' 상태로 변경된 경우 -> 출간요청됨 이벤트 발행
        else if ("PUBLICATION_REQUESTED".equals(this.status)) {
            PublicationRequested publicationRequested = new PublicationRequested(this);
            publicationRequested.publishAfterCommit();
        }

    }

    // <<< Clean Arch / Port Method
    public void publicationRequest(PublicationRequestCommand publicationRequestCommand) {

        // 1. 현재 원고의 상태를 확. "SAVED" 또는 "REGISTERED" 상태여야만 출간 요청 가능
        if (!"SAVED".equals(this.status) && !"REGISTERED".equals(this.status)) {
            throw new IllegalStateException("Manuscript must be in SAVED or REGISTERED status to request publication.");
        }
        // 2. 원고 상태를 'PUBLICATION_REQUESTED'로 변경
        this.setStatus("PUBLICATION_REQUESTED");
        this.setLastModifiedAt(LocalDateTime.now()); // 최종 수정 시각 업데이트

        // 이벤트 발행은 @PostUpdate 콜백에서 처리.
        // ManuscriptRepository.save(this) 호출 시 @PostUpdate가 작동하여 이벤트가 발행
    }
    //>>> Clean Arch / Port Method


    // <<< Clean Arch / Port Method
    public static Manuscript registerNewManuscript(ManuscriptRegistrationCommand manuscriptRegistrationCommand) {
        Manuscript manuscript = new Manuscript();
        manuscript.setTitle(manuscriptRegistrationCommand.getTitle());
        manuscript.setContent(manuscriptRegistrationCommand.getContent());
        manuscript.setAuthorId(manuscriptRegistrationCommand.getAuthorId());
        manuscript.setStatus("REGISTERED"); // 원고 등록 시 초기 상태
        manuscript.setLastModifiedAt(LocalDateTime.now());

        // 생성된 원고 객체 반환
        return manuscript;
    }
    //>>> Clean Arch / Port Method



    //<<< Clean Arch / Port Method
    public void manuscriptSave(ManuscriptSaveCommand manuscriptSaveCommand) {
        // 1. 커맨드로부터 받은 정보로 원고의 내용을 업데이트
        this.setTitle(manuscriptSaveCommand.getTitle());
        this.setContent(manuscriptSaveCommand.getContent());
        this.setSummary(manuscriptSaveCommand.getSummary()); 
        this.setKeywords(manuscriptSaveCommand.getKeywords());

        // 2. 상태를 'SAVED'로 변경
        this.setStatus("SAVED");
        this.setLastModifiedAt(LocalDateTime.now()); // 최종 수정 시각 업데이트

        // 이벤트 발행은 @PostUpdate 콜백에서 처리. 여기서 이벤트 발행 불필요
        // ManuscriptRepository.save(this) 호출 시 @PostUpdate가 작동하여 이벤트가 발행
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
