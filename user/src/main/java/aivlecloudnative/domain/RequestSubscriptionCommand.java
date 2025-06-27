package aivlecloudnative.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class RequestSubscriptionCommand {

    private String email;
    private String userName;
}
