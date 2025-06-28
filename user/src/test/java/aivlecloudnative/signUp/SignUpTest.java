package aivlecloudnative.signUp;

import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.SignUpCommand;
import aivlecloudnative.infra.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class SignUpTest {

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
}
