package aivlecloudnative.domain;

import lombok.Data;

@Data
public class SignUpCommand {
    private String userName;
    private String email;

}
