package aivlecloudnative.signUp;

import aivlecloudnative.application.UserService;
import aivlecloudnative.domain.BookViewed;
import aivlecloudnative.domain.OutboxMessage;
import aivlecloudnative.domain.OutboxMessageRepository;
import aivlecloudnative.domain.RequestContentAccessCommand;
import aivlecloudnative.domain.RequestSubscriptionCommand;
import aivlecloudnative.domain.SignUpCommand;
import aivlecloudnative.domain.User;
import aivlecloudnative.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxMessageRepository outboxMessageRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        userService = new UserService(userRepository, outboxMessageRepository, objectMapper);
    }

    @Test
    @DisplayName("존재하지 않는 userId로 요청 시 예외 발생")
    void requestSubscription_shouldThrowException_ifUserNotFound() {
        // given
        Long invalidUserId = 999L;
        RequestSubscriptionCommand command = new RequestSubscriptionCommand();
        command.setUserId(invalidUserId);

        when(userRepository.findById(invalidUserId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.requestSubscription(command));
        verify(userRepository).findById(invalidUserId);
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 실패")
    void signUp_shouldThrow_ifEmailExists() {
        // given
        SignUpCommand cmd = new SignUpCommand();
        cmd.setEmail("test@example.com");

        when(userRepository.existsByEmail(cmd.getEmail())).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.signUp(cmd));
    }

    @Test
    @DisplayName("회원가입 성공 시 Outbox 저장")
    void signUp_shouldSaveOutbox() {
        // given
        SignUpCommand cmd = new SignUpCommand();
        cmd.setEmail("test@example.com");
        cmd.setUserName("홍길동");
        cmd.setIsKt(true);

        when(userRepository.existsByEmail(cmd.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        userService.signUp(cmd);

        // then
        verify(outboxMessageRepository).save(any(OutboxMessage.class));
    }

    @Test
    @DisplayName("KT 유저가 열람 신청 시 AccessRequestedAsSubscriber 이벤트 저장")
    void requestContentAccess_shouldSaveKtEvent() {
        // given
        User user = new User();
        user.setId(1L);
        user.setIsKt(true);

        RequestContentAccessCommand cmd = new RequestContentAccessCommand();
        cmd.setUserId(1L);
        cmd.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        userService.requestContentAccess(cmd);

        // then
        verify(outboxMessageRepository).save(argThat(msg -> msg.getEventType().equals("AccessRequestedAsSubscriber")));
    }

    @Test
    @DisplayName("비 KT 유저가 열람 신청 시 AccessRequestedWithPoints 이벤트 저장")
    void requestContentAccess_shouldSavePointEvent() {
        // given
        User user = new User();
        user.setId(1L);
        user.setIsKt(false);

        RequestContentAccessCommand cmd = new RequestContentAccessCommand();
        cmd.setUserId(1L);
        cmd.setBookId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        userService.requestContentAccess(cmd);

        // then
        verify(outboxMessageRepository).save(argThat(msg -> msg.getEventType().equals("AccessRequestedWithPoints")));
    }

    @Test
    @DisplayName("BookViewed 이벤트로 독서 기록이 추가됨")
    void updateBookRead_shouldAddBookToHistory() {
        // given
        User user = new User();
        user.setId(1L);
        user.setMyBookHistory(new ArrayList<>());

        BookViewed event = new BookViewed();
        event.setUserId(1L);
        event.setBookId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        userService.updateBookRead(event);

        // then
        assert user.getMyBookHistory().contains(10L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("구독 상태를 반환한다")
    void getSubscriptionStatus_shouldReturnCorrectValue() {
        User user = new User();
        user.setHasActiveSubscription(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.getSubscriptionStatus(1L);

        assert result;
    }

    @Test
    @DisplayName("열람 기록을 반환한다")
    void getContentHistory_shouldReturnBookHistory() {
        User user = new User();
        List<Long> history = List.of(1L, 2L, 3L);
        user.setMyBookHistory(history);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<Long> result = userService.getContentHistory(1L);

        assert result.size() == 3;
    }
}
