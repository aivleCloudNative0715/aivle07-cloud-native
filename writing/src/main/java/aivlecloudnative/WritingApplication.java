package aivlecloudnative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext; 

@SpringBootApplication
@EnableFeignClients
public class WritingApplication {

    public static ApplicationContext applicationContext; 

    public static void main(String[] args) {
        applicationContext =
            SpringApplication.run(WritingApplication.class, args);
    }
}
