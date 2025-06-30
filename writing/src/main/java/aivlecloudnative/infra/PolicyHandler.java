//>>> Clean Arch / Inbound Adaptor

package aivlecloudnative.infra;

import aivlecloudnative.domain.ManuscriptRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.context.annotation.Bean; 
import java.util.function.Consumer; 

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional 
public class PolicyHandler {

    @Autowired
    ManuscriptRepository manuscriptRepository; 

    @Bean
    public Consumer<String> whatever() { 
        return eventString -> {
        };
    }
}
//>>> Clean Arch / Inbound Adaptor