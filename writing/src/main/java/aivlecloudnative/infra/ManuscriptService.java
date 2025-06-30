package aivlecloudnative.domain; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.util.Optional;


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

        Manuscript newManuscript = Manuscript.registerNewManuscript(command);

        // 애그리거트 저장
        return manuscriptRepository.save(newManuscript);
    }

    // 기존 원고 수정 유스케이스
    public Manuscript saveManuscript(Long id, ManuscriptSaveCommand command) throws Exception {
        // 커맨드 데이터 유효성 검증
        if (command.getTitle() == null || command.getContent() == null) {
             throw new IllegalArgumentException("Title and content must not be null for manuscript save.");
        }

        // ID로 기존 Manuscript 조회
        Optional<Manuscript> optionalManuscript = manuscriptRepository.findById(id);
        Manuscript manuscript = optionalManuscript.orElseThrow(() -> new Exception("Manuscript not found with ID: " + id));

        // 애그리거트의 비즈니스 메서드 호출
        // manuscript.manuscriptSave() 메서드 내부에서 ManuscriptSaved 이벤트를 발행
        manuscript.manuscriptSave(command);

        // 변경된 애그리거트 저장
        return manuscriptRepository.save(manuscript);
    }

    // 출간요청 유스케이스
    public Manuscript requestPublication(PublicationRequestCommand command) throws Exception {
        // 커맨드 유효성 검증 (ID는 필수)
        if (command.getId() == null) {
            throw new IllegalArgumentException("Manuscript ID must not be null for publication request.");
        }

        // ID로 기존 Manuscript 조회
        Optional<Manuscript> optionalManuscript = manuscriptRepository.findById(command.getId());
        Manuscript manuscript = optionalManuscript.orElseThrow(() -> new Exception("Manuscript not found with ID: " + command.getId()));

        // 애그리거트의 비즈니스 메서드 호출
        // manuscript.publicationRequest() 메서드 내부에서 PublicationRequested 이벤트를 발행
        manuscript.publicationRequest(command);

        // 변경된 애그리거트 저장 
        return manuscriptRepository.save(manuscript);
    }
}