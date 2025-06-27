package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        HttpServletRequest request,
        HttpServletResponse response,
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
        HttpServletRequest request,
        HttpServletResponse response,
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
        HttpServletRequest request,
        HttpServletResponse response
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
