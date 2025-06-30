package aivlecloudnative.domain; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

@Service
@Transactional 
public class ManuscriptService {

    @Autowired
    private ManuscriptRepository manuscriptRepository;

    // 새로운 원고 등록 유스케이스
    public Manuscript registerManuscript(ManuscriptRegistrationCommand command) {
        // 커맨드 데이터 유효성 검증
        if (command.getAuthorId() == null || command.getTitle() == null || command.getContent() == null) {
            throw new IllegalArgumentException("Author ID, title, and content must not be null for manuscript registration.");
        }

        // 엔티티의 팩토리 메서드를 통해 객체 생성 및 초기 필드 설정
        Manuscript newManuscript = Manuscript.createNew(
            command.getAuthorId(),
            command.getTitle(),
            command.getContent(),
            command.getSummary(), // <-- 추가
            command.getKeywords() // <-- 추가
        );

        return manuscriptRepository.save(newManuscript); // DB 저장 및 @PostPersist 이벤트 발생
    }

    // 기존 원고 수정 유스케이스
    public Manuscript saveManuscript(Long id, ManuscriptSaveCommand command) {
        
        // 커맨드 데이터 유효성 검증
        if (command.getTitle() == null || command.getContent() == null) {
            throw new IllegalArgumentException("Title and content must not be null for manuscript save.");
        }

        // 기존 원고를 찾아서 업데이트
        Manuscript existingManuscript = manuscriptRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Manuscript not found"));

        // 권한 확인
        if (!existingManuscript.getAuthorId().equals(command.getAuthorId())) {
            throw new SecurityException("You do not have permission to modify this manuscript.");
        }

        // 엔티티의 비즈니스 메서드를 호출하여 내용 업데이트 및 상태 변경
        existingManuscript.setTitle(command.getTitle()); // 단순 필드 업데이트는 Setter 사용
        existingManuscript.setContent(command.getContent());
        existingManuscript.setSummary(command.getSummary());
        existingManuscript.setKeywords(command.getKeywords());
        existingManuscript.changeStatusToSaved(); // 상태 변경 로직은 엔티티 내부에서!

        return manuscriptRepository.save(existingManuscript); // DB 저장 및 @PostUpdate 이벤트 발생
    }

    // 출간 요청 유스케이스
    public Manuscript requestPublication(PublicationRequestCommand command) {

        // Command 유효성 검증
        if (command.getId() == null) {
            throw new IllegalArgumentException("Manuscript ID must not be null for publication request.");
        }

        // ID로 기존 Manuscript 조회
        Manuscript manuscript = manuscriptRepository.findById(command.getManuscriptId())
                                .orElseThrow(() -> new IllegalArgumentException("Manuscript not found for publication request."));

        // 권한 확인
        if (!manuscript.getAuthorId().equals(command.getAuthorId())) {
            throw new SecurityException("You do not have permission to publish this manuscript.");
        }

        // 엔티티의 비즈니스 메서드를 호출하여 출간 요청 처리
        manuscript.requestPublication(); // 엔티티 내부에서 상태 변경 및 lastModifiedAt 업데이트

        return manuscriptRepository.save(manuscript); // DB 저장 및 @PostUpdate 이벤트 발생
    }
}