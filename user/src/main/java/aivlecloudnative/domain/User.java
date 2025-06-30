package aivlecloudnative.domain;

import aivlecloudnative.UserApplication;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    private String userName;

    private Boolean hasActiveSubscription;

    private String message;

    private Long subscriptionDueDate;

    @ElementCollection
    private List<Long> myBookHistory;

    private Boolean isKt;

    private String password;

    public static UserRepository repository() {
        return UserApplication.applicationContext.getBean(
            UserRepository.class
        );
    }

    public void signUp(SignUpCommand signUpCommand) {
        this.userName = signUpCommand.getUserName();
        this.email = signUpCommand.getEmail();
        this.isKt = signUpCommand.getIsKt();
    }

    public void addBookToHistory(Long bookId) {
        if (this.myBookHistory == null) {
            this.myBookHistory = new ArrayList<>();
        }

        if (!this.myBookHistory.contains(bookId)) {
            this.myBookHistory.add(bookId);
        }
    }

}
//>>> DDD / Aggregate Root
