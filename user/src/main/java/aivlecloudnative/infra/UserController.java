package aivlecloudnative.infra;
import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/users")
@Transactional
public class UserController {
    @Autowired
    private UserService userService;

    //TODO: 비밀번호 추가 및 검사
    @RequestMapping(
            value = "/signup",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8"
    )
    public User signUp(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody SignUpCommand signUpCommand
    ) throws Exception {
        System.out.println("##### /user/signUp  called #####");
        return userService.signUp(signUpCommand);
    }

    @RequestMapping(value = "/request-subscription",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User requestSubscription(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody RequestSubscriptionCommand requestSubscriptionCommand) throws Exception {
            System.out.println("##### /user/requestSubscription  called #####");
            return userService.requestSubscription(requestSubscriptionCommand);
    }

    @RequestMapping(value = "/request-content-access",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User requestContentAccess(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody RequestContentAccessCommand requestContentAccessCommand) throws Exception {
            System.out.println("##### /user/requestContentAccess  called #####");
            return userService.requestContentAccess(requestContentAccessCommand);
    }

    @RequestMapping(
            value = "/{id}/is-subscribed",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8"
    )
    public boolean getUserSubscriptionsStatus(
            @PathVariable("id") Long id
    ) throws Exception {
        System.out.println("##### /users/{id}/is-subscribed called #####");
        return userService.getSubscriptionStatus(id);
    }

    @RequestMapping(
            value = "/{id}/content-histories",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8"
    )
    public List<Long> getUserContentHistories(
            @PathVariable("id") Long id
    ) throws Exception {
        System.out.println("##### /users/{id}/content-histories called #####");
        return userService.getContentHistory(id);
    }
}
//>>> Clean Arch / Inbound Adaptor
