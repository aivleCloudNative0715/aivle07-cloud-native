package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 추가

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSignedUp extends AbstractEvent {
    private String userId;
    private String email;
    private String userName;
    private String message;
    private Boolean isKT;
}