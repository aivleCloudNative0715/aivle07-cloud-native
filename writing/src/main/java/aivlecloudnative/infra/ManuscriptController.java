package aivlecloudnative.infra;

import java.util.Optional;

import aivlecloudnative.domain.Manuscript;
import aivlecloudnative.domain.ManuscriptRegistrationCommand;
import aivlecloudnative.domain.ManuscriptSaveCommand;
import aivlecloudnative.domain.PublicationRequestCommand;
import aivlecloudnative.domain.ManuscriptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import aivlecloudnative.domain.ManuscriptListRepository;
import aivlecloudnative.domain.ManuscriptList;

// 원고 관련 HTTP 요청을 처리하는 REST 컨트롤러
@RestController
@RequestMapping(value = "/manuscripts") // 모든 엔드포인트의 기본 경로 설정
public class ManuscriptController {


    @Autowired
    private ManuscriptService manuscriptService;

    @Autowired
    private ManuscriptListRepository manuscriptListRepository;

    @PostMapping("/registration")
    public ResponseEntity<Manuscript> manuscriptRegistration(
        @Valid @RequestBody ManuscriptRegistrationCommand cmd
    ) {
        System.out.println("##### /manuscripts/registration called #####");
        // 서비스 계층으로 로직 위임
        Manuscript result = manuscriptService.registerManuscript(cmd);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/save")
    public ResponseEntity<Manuscript> manuscriptSave(
        @PathVariable(value = "id") Long id,
        @Valid @RequestBody ManuscriptSaveCommand cmd
    ) {
        System.out.println("##### /manuscripts/{id}/save called #####");

        // 서비스 계층으로 로직 위임
        Manuscript result = manuscriptService.saveManuscript(id, cmd);
        return new ResponseEntity<>(result, HttpStatus.OK); // 성공 시 200 OK
    }


    @PostMapping("/publication-request")
    public ResponseEntity<Manuscript> publicationRequest(
        @Valid @RequestBody PublicationRequestCommand cmd
    ) {
        System.out.println("##### /manuscripts/publication-request called #####");
        // 서비스 계층으로 로직 위임
        Manuscript result = manuscriptService.requestPublication(cmd);
        return new ResponseEntity<>(result, HttpStatus.OK); // 성공 시 200 OK
    }

    // 리드 모델(Read Model) 조회 엔드포인트

    // 1. 특정 작가의 모든 원고 목록 조회: GET /manuscriptLists/{authorId}
    @GetMapping("/{authorId}") // 경로 변수 authorId를 사용
    public ResponseEntity<List<ManuscriptList>> getManuscriptsByAuthor(
            @PathVariable String authorId // authorId를 경로 변수로 받음
    ) {
        System.out.println("##### /manuscriptLists/" + authorId + " called (GET all by authorId) #####");

        // 경로 변수로 받았으므로 null/empty 체크는 일반적으로 불필요합니다 (경로가 비어있으면 404가 발생)
        // 만약 authorId의 특정 패턴 유효성 검사가 필요하면 여기에 추가 가능

        System.out.println("##### Fetching manuscripts for authorId: " + authorId + " #####");
        List<ManuscriptList> manuscriptLists = manuscriptListRepository.findByAuthorId(authorId);

        if (manuscriptLists.isEmpty()) {
            // 해당 작가의 원고가 없을 경우 404 Not Found 반환
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(manuscriptLists, HttpStatus.OK);
        }
    }

    // 2. 특정 작가의 특정 원고 상세 조회: GET /manuscriptLists/{authorId}/{manuscriptId}
    @GetMapping("/{authorId}/{manuscriptId}") // authorId와 manuscriptId를 모두 경로 변수로 사용
    public ResponseEntity<ManuscriptList> getSpecificManuscriptByAuthor(
            @PathVariable String authorId,
            @PathVariable Long manuscriptId
    ) {
        System.out.println("##### /manuscriptLists/" + authorId + "/" + manuscriptId + " called (GET by authorId and manuscriptId) #####");

        // ManuscriptListRepository에 추가된 findByAuthorIdAndId 메서드 사용
        Optional<ManuscriptList> manuscriptListOptional = manuscriptListRepository.findByAuthorIdAndManuscriptId(authorId, manuscriptId);

        if (manuscriptListOptional.isPresent()) {
            return new ResponseEntity<>(manuscriptListOptional.get(), HttpStatus.OK);
        } else {
            // 해당 작가의 원고가 아니거나 원고 ID가 존재하지 않을 경우 404 Not Found 반환
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}