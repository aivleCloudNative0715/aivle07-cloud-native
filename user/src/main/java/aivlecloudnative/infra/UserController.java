package aivlecloudnative.infra;
import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.*;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/users")
@Transactional
public class UserController {
    @Autowired
    private UserService userService;

    //TODO: 비밀번호 추가 및 검사
    @RequestMapping(
            value = "/users/signup",
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

    @RequestMapping(value = "/users/requestsubscription",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User requestSubscription(HttpServletRequest request, HttpServletResponse response, 
        @RequestBody RequestSubscriptionCommand requestSubscriptionCommand) throws Exception {
            System.out.println("##### /user/requestSubscription  called #####");
            User user = new User();
            user.requestSubscription(requestSubscriptionCommand);
//            userRepository.save(user);
            return user;
    }
    @RequestMapping(value = "/users/requestcontentaccess",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User requestContentAccess(HttpServletRequest request, HttpServletResponse response, 
        @RequestBody RequestContentAccessCommand requestContentAccessCommand) throws Exception {
            System.out.println("##### /user/requestContentAccess  called #####");
            User user = new User();
            user.requestContentAccess(requestContentAccessCommand);
//            userRepository.save(user);
            return user;
    }
}
//>>> Clean Arch / Inbound Adaptor
