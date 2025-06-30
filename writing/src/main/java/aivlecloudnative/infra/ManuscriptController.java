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

import javax.validation.Valid; 


// 원고 관련 HTTP 요청을 처리하는 REST 컨트롤러
@RestController
@RequestMapping(value = "/manuscripts") // 모든 엔드포인트의 기본 경로 설정
public class ManuscriptController {


    @Autowired
    private ManuscriptService manuscriptService;


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

}