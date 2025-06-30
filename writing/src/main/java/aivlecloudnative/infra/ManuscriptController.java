package aivlecloudnative.infra;

import aivlecloudnative.domain.Manuscript;
import aivlecloudnative.domain.ManuscriptRegistrationCommand;
import aivlecloudnative.domain.ManuscriptSaveCommand;
import aivlecloudnative.domain.PublicationRequestCommand;
import aivlecloudnative.domain.ManuscriptService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; 

//<<< Clean Arch / Inbound Adaptor

// 원고 관련 HTTP 요청을 처리하는 REST 컨트롤러
@RestController
@RequestMapping(value = "/manuscripts") // 모든 엔드포인트의 기본 경로 설정
public class ManuscriptController {

    // ManuscriptRepository 대신 ManuscriptService를 주입받도록 한다
    @Autowired
    private ManuscriptService manuscriptService;

    // POST /manuscripts/publication-request
    @PostMapping("/publication-request") 
    public ResponseEntity<Manuscript> publicationRequest(
        @RequestBody PublicationRequestCommand publicationRequestCommand
    ) {
        System.out.println("##### /manuscripts/publication-request called #####");
        try {
            // 서비스 계층으로 로직 위임
            Manuscript result = manuscriptService.requestPublication(publicationRequestCommand);
            return new ResponseEntity<>(result, HttpStatus.OK); // 성공 시 200 OK
        } catch (IllegalArgumentException e) {
            // 커맨드 유효성 검증 실패 시 400 Bad Request
            System.err.println("Publication request failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 그 외 예외 발생 시 500 Internal Server Error
            System.err.println("Publication request failed due to an unexpected error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST /manuscripts/registration
    @PostMapping("/registration") 
    public ResponseEntity<Manuscript> manuscriptRegistration(
        @RequestBody ManuscriptRegistrationCommand manuscriptRegistrationCommand
    ) {
        System.out.println("##### /manuscripts/registration called #####");
        try {
            // 서비스 계층으로 로직 위임
            Manuscript result = manuscriptService.registerManuscript(manuscriptRegistrationCommand);
            return new ResponseEntity<>(result, HttpStatus.CREATED); // 생성 성공 시 201 Created
        } catch (IllegalArgumentException e) {
            System.err.println("Manuscript registration failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Manuscript registration failed due to an unexpected error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT /manuscripts/{id}/save
    @PutMapping("/{id}/save") 
    public ResponseEntity<Manuscript> manuscriptSave(
        @PathVariable(value = "id") Long id,
        @RequestBody ManuscriptSaveCommand manuscriptSaveCommand
    ) {
        System.out.println("##### /manuscripts/{id}/save called #####");
        try {
            // 커맨드 객체에 ID를 설정 (필요하다면)
            manuscriptSaveCommand.setId(id); // ManuscriptSaveCommand에 id 필드가 있다면

            // 서비스 계층으로 로직 위임
            Manuscript result = manuscriptService.saveManuscript(id, manuscriptSaveCommand);
            return new ResponseEntity<>(result, HttpStatus.OK); // 성공 시 200 OK
        } catch (IllegalArgumentException e) {
            System.err.println("Manuscript save failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 원고를 찾을 수 없거나 다른 예외 발생 시
            System.err.println("Manuscript save failed: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found (만약 "No Entity Found"일 경우)
        }
    }

}