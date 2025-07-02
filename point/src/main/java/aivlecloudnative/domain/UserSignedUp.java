package aivlecloudnative.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSignedUp {
    private String userId;
    private String email;
    private String userName;
    private String message;
    private Boolean isKT; // KT 요금제 가입자인지 확인 (true/false)
}