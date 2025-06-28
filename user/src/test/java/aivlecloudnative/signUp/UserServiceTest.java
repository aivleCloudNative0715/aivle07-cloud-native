package aivlecloudnative.signUp;

import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.RequestSubscriptionCommand;
import aivlecloudnative.domain.SignUpCommand;
import aivlecloudnative.domain.User;
import aivlecloudnative.domain.UserRepository;
import aivlecloudnative.infra.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("HTTP POST 회원가입 테스트")
    void signup_should_return200_when_valid() throws Exception {
        // given
        SignUpCommand command = new SignUpCommand();
        command.setUserName("홍길동");
        command.setEmail("test@example.com");

        // when - void 메서드는 별도 stub 필요 없음

        // then
        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        // verify signUp 호출 여부
        Mockito.verify(userService).signUp(any(SignUpCommand.class));
    }

    @Test
    @DisplayName("HTTP POST 구독 요청 테스트")
    void requestSubscription_should_return200_when_valid() throws Exception {
        // given
        RequestSubscriptionCommand command = new RequestSubscriptionCommand();
        command.setUser_id(1L);

        User dummyUser = new User(); // 반환할 유저 객체 (가짜)
        dummyUser.setHasActiveSubscription(true);

        Mockito.when(userService.requestSubscription(any(RequestSubscriptionCommand.class)))
                .thenReturn(dummyUser);

        // when & then
        mockMvc.perform(post("/users/requestsubscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        Mockito.verify(userService).requestSubscription(any(RequestSubscriptionCommand.class));
    }

    @Test
    @DisplayName("존재하지 않는 userId로 요청 시 예외 발생")
    void requestSubscription_shouldThrowException_ifUserNotFound() {
        // given
        Long invalidUserId = 999L;
        RequestSubscriptionCommand command = new RequestSubscriptionCommand();
        command.setUser_id(invalidUserId);

        Mockito.when(userRepository.findById(invalidUserId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.requestSubscription(command));

        Mockito.verify(userRepository).findById(invalidUserId);
    }
}
