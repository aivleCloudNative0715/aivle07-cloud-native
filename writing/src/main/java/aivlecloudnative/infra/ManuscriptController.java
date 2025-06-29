package aivlecloudnative.infra;

import aivlecloudnative.domain.*; 
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest; // 변경: javax -> jakarta
import jakarta.servlet.http.HttpServletResponse; // 변경: javax -> jakarta
import javax.transaction.Transactional; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional; 

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/manuscripts") 
@Transactional 
public class ManuscriptController {

    @Autowired
    ManuscriptRepository manuscriptRepository;

    @RequestMapping(
        value = "/manuscripts/publicationrequest",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public Manuscript publicationRequest(
        HttpServletRequest request,  // Jakarta Servlet API 사용
        HttpServletResponse response, // Jakarta Servlet API 사용
        @RequestBody PublicationRequestCommand publicationRequestCommand
    ) throws Exception {
        System.out.println(
            "##### /manuscript/publicationRequest  called #####"
        );
        Manuscript manuscript = new Manuscript();
        manuscript.publicationRequest(publicationRequestCommand);
        manuscriptRepository.save(manuscript);
        return manuscript;
    }

    @RequestMapping(
        value = "/manuscripts/manuscriptregistration",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public Manuscript manuscriptRegistration(
        HttpServletRequest request,  // Jakarta Servlet API 사용
        HttpServletResponse response, // Jakarta Servlet API 사용
        @RequestBody ManuscriptRegistrationCommand manuscriptRegistrationCommand
    ) throws Exception {
        System.out.println(
            "##### /manuscript/manuscriptRegistration  called #####"
        );
        Manuscript manuscript = new Manuscript();
        manuscript.manuscriptRegistration(manuscriptRegistrationCommand);
        manuscriptRepository.save(manuscript);
        return manuscript;
    }

    @RequestMapping(
        value = "/manuscripts/{id}/manuscriptsave",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Manuscript manuscriptSave(
        @PathVariable(value = "id") Long id,
        @RequestBody ManuscriptSaveCommand manuscriptSaveCommand,
        HttpServletRequest request, // Jakarta Servlet API 사용
        HttpServletResponse response // Jakarta Servlet API 사용
    ) throws Exception {
        System.out.println("##### /manuscript/manuscriptSave  called #####");
        Optional<Manuscript> optionalManuscript = manuscriptRepository.findById(
            id
        );

        optionalManuscript.orElseThrow(() -> new Exception("No Entity Found"));
        Manuscript manuscript = optionalManuscript.get();
        manuscript.manuscriptSave(manuscriptSaveCommand);

        manuscriptRepository.save(manuscript);
        return manuscript;
    }
}
//>>> Clean Arch / Inbound Adaptor