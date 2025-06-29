package aivlecloudnative.application;

import aivlecloudnative.domain.RequestSubscriptionCommand;
import aivlecloudnative.domain.SignUpCommand;
import aivlecloudnative.domain.SubscriberSignedUp;
import aivlecloudnative.domain.User;
import aivlecloudnative.domain.UserRepository;
import aivlecloudnative.infra.AbstractEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AbstractEventPublisher eventPublisher;

    public User signUp(SignUpCommand cmd) {
        if (userRepository.existsByEmail(cmd.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User();
        user.signUp(cmd);

        userRepository.save(user);

        SubscriberSignedUp subscriberSignedUp = new SubscriberSignedUp(user);
        eventPublisher.publish("event-out", subscriberSignedUp, "SubscriberSignedUp");

        return user;
    }

    public User requestSubscription(RequestSubscriptionCommand command) {
        Long userId = command.getUser_id();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다: " + userId));

        user.setHasActiveSubscription(true);
        userRepository.save(user);
        return user;
    }
}
