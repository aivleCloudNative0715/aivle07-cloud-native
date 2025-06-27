package aivlecloudnative.infra;
import aivlecloudnative.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/users")
@Transactional
public class UserController {
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/users/signup",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User signUp(HttpServletRequest request, HttpServletResponse response, 
        ) throws Exception {
            System.out.println("##### /user/signUp  called #####");
            User user = new User();
            user.signUp();
            userRepository.save(user);
            return user;
    }
    @RequestMapping(value = "/users/requestsubscription",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public User requestSubscription(HttpServletRequest request, HttpServletResponse response, 
        @RequestBody RequestSubscriptionCommand requestSubscriptionCommand) throws Exception {
            System.out.println("##### /user/requestSubscription  called #####");
            User user = new User();
            user.requestSubscription(requestSubscriptionCommand);
            userRepository.save(user);
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
            userRepository.save(user);
            return user;
    }
}
//>>> Clean Arch / Inbound Adaptor
