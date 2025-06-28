package aivlecloudnative.domain;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestSubscriptionCommand {
    @NotNull(message = "아이디를 입력해주세요")
    private Long user_id;
}
