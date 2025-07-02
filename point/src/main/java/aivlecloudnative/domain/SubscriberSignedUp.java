package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import lombok.EqualsAndHashCode;
import java.util.*;
import lombok.*;
import java.time.LocalDateTime;

// 회원가입됨 이벤트 클래스
public class SubscriberSignedUp extends AbstractEvent {

    private Long id; // 이벤트 자체의 고유 ID 또는 (이벤트 발행 서비스의) 사용자 ID
    private String email; // 이메일 (주석 처리)
    private String userName; // 사용자 이름
    private String message; // 메시지 (주석 처리)
    private Boolean isKt; // KT 멤버 여부
    private Long userId; // <-- 포인트 서비스에서 사용할 사용자 ID (이벤트의 id와 다를 수 있다면 명시)

    // 기본 생성자 (Jackson 직렬화를 위해 필요)
    public SubscriberSignedUp() {
        super();
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsKt() {
        return isKt;
    }

    public void setIsKt(Boolean isKt) {
        this.isKt = isKt;
    }

    public Long getUserId() { // <-- userId 필드에 대한 Getter
        return userId;
    }

    public void setUserId(Long userId) { // <-- userId 필드에 대한 Setter
        this.userId = userId;
    }

    @Override
    public boolean validate() {
        // 필수 필드 유효성 검사
        // id와 isKt, userId는 반드시 필요하다고 가정합니다.
        return super.validate() && id != null && isKt != null && userId != null;
    }
}