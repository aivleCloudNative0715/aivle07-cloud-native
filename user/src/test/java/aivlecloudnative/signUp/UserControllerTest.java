package aivlecloudnative.signUp;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.RequestContentAccessCommand;
import aivlecloudnative.domain.RequestSubscriptionCommand;
import aivlecloudnative.domain.SignUpCommand;
import aivlecloudnative.domain.User;
import aivlecloudnative.infra.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // -----------------------------
    // ✅ 회원가입 테스트
    // -----------------------------

    @Test
    @DisplayName("회원가입 성공 시 200 응답")
    void signup_should_return200_when_valid() throws Exception {
        SignUpCommand command = new SignUpCommand();
        command.setUserName("홍길동");
        command.setEmail("test@example.com");
        command.setIsKt(true);

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        Mockito.verify(userService).signUp(any(SignUpCommand.class));
    }

    @Test
    @DisplayName("회원가입 실패 시 400 응답 (isKt 누락)")
    void signup_should_return400_when_isKt_missing() throws Exception {
        SignUpCommand command = new SignUpCommand();
        command.setUserName("홍길동");
        command.setEmail("test@example.com");
        // isKt 누락

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------
    // ✅ 구독 요청 테스트
    // -----------------------------

    @Test
    @DisplayName("구독 요청 성공 시 200 응답")
    void requestSubscription_should_return200_when_valid() throws Exception {
        RequestSubscriptionCommand command = new RequestSubscriptionCommand();
        command.setUserId(1L);

        User dummy = new User();
        dummy.setHasActiveSubscription(true);

        Mockito.when(userService.requestSubscription(any()))
                .thenReturn(dummy);

        mockMvc.perform(post("/users/request-subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        Mockito.verify(userService).requestSubscription(any());
    }

    @Test
    @DisplayName("구독 요청 실패 시 400 응답 (userId 누락)")
    void requestSubscription_should_return400_when_userId_missing() throws Exception {
        RequestSubscriptionCommand command = new RequestSubscriptionCommand();

        mockMvc.perform(post("/users/request-subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------
    // ✅ 열람 신청(request-content-access) 테스트
    // -----------------------------

    @Test
    @DisplayName("열람 신청 성공 시 200 응답")
    void requestContentAccess_should_return200_when_valid() throws Exception {
        RequestContentAccessCommand command = new RequestContentAccessCommand();
        command.setUserId(1L);
        command.setBookId(2L);

        User dummy = new User();
        dummy.addBookToHistory(2L);

        Mockito.when(userService.requestContentAccess(any()))
                .thenReturn(dummy);

        mockMvc.perform(post("/users/request-content-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        Mockito.verify(userService).requestContentAccess(any());
    }

    @Test
    @DisplayName("열람 신청 실패 시 400 응답 (userId, bookId 누락)")
    void requestContentAccess_should_return400_when_params_missing() throws Exception {
        RequestContentAccessCommand command = new RequestContentAccessCommand(); // 두 값 모두 누락

        mockMvc.perform(post("/users/request-content-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------
    // ✅ 구독 상태 조회(is-subscribed) 테스트
    // -----------------------------

    @Test
    @DisplayName("구독 상태 조회 성공 시 200 응답과 true 반환")
    void getSubscriptionStatus_should_return200_and_true() throws Exception {
        Mockito.when(userService.getSubscriptionStatus(1L)).thenReturn(true);

        mockMvc.perform(get("/users/1/is-subscribed"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        Mockito.verify(userService).getSubscriptionStatus(1L);
    }

    // -----------------------------
    // ✅ 열람 이력 조회(content-histories) 테스트
    // -----------------------------

    @Test
    @DisplayName("열람 이력 조회 성공 시 200 응답과 JSON 배열 반환")
    void getContentHistories_should_return200_and_list() throws Exception {
        List<Long> histories = List.of(2L, 5L);
        Mockito.when(userService.getContentHistory(1L)).thenReturn(histories);

        mockMvc.perform(get("/users/1/content-histories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(histories)));

        Mockito.verify(userService).getContentHistory(1L);
    }
}
