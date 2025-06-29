package aivlecloudnative.infra;

// import aivlecloudnative.config.kafka.KafkaProcessor; // <-- 제거
// import aivlecloudnative.domain.*; 
import aivlecloudnative.domain.ManuscriptList;
import aivlecloudnative.domain.ManuscriptRegisterd;
import aivlecloudnative.domain.ManuscriptSaved;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.stream.annotation.StreamListener; // <-- 제거
// import org.springframework.messaging.handler.annotation.Payload; // <-- @Bean 방식에서는 직접 사용 안함
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean; // @Bean 어노테이션을 위해 추가
import java.util.function.Consumer; // Consumer 인터페이스를 위해 추가
import java.util.List;
import java.io.IOException;
import java.util.Optional; 

@Service 
public class ManuscriptListViewHandler {

    @Autowired
    private ManuscriptListRepository manuscriptListRepository;

    // --- ManuscriptRegisterd 이벤트를 처리하는 Consumer Bean ---
    @Bean
    public Consumer<ManuscriptRegisterd> whenManuscriptRegisterd_then_CREATE_1() {
        return manuscriptRegisterd -> {
            try {
                if (!manuscriptRegisterd.validate()) return;

                // view 객체 생성
                ManuscriptList manuscriptList = new ManuscriptList();
                // view 객체에 이벤트의 Value 를 set 함
                manuscriptList.setManuscriptId(manuscriptRegisterd.getId());
                manuscriptList.setManuscriptTitle(manuscriptRegisterd.getTitle());
                manuscriptList.setManuscriptContent(
                    manuscriptRegisterd.getContent()
                );
                manuscriptList.setManuscriptStatus(manuscriptRegisterd.getStatus());
                // ManuscriptList의 lastModifiedAt도 LocalDateTime이라고 가정
                manuscriptList.setLastModifiedAt(
                    manuscriptRegisterd.getLastModifiedAt() // String.valueOf() 제거, 직접 할당
                );
                manuscriptList.setAuthorId(manuscriptRegisterd.getAuthorId()); // AutorId -> AuthorId로 변경
                // 중복 호출 제거
                // manuscriptList.setAutorId(manuscriptRegisterd.getAuthorId());

                // view 레파지 토리에 save
                manuscriptListRepository.save(manuscriptList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // --- ManuscriptSaved 이벤트를 처리하는 Consumer Bean ---
    @Bean
    public Consumer<ManuscriptSaved> whenManuscriptSaved_then_UPDATE_1() {
        return manuscriptSaved -> {
            try {
                if (!manuscriptSaved.validate()) return;
                // view 객체 조회

                // findByAutorId -> findByAuthorId로 변경
                List<ManuscriptList> manuscriptListList = manuscriptListRepository.findByAuthorId(
                    manuscriptSaved.getAuthorId()
                );
                for (ManuscriptList manuscriptList : manuscriptListList) {
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    manuscriptList.setManuscriptId(manuscriptSaved.getId());
                    manuscriptList.setManuscriptTitle(manuscriptSaved.getTitle());
                    manuscriptList.setManuscriptContent(
                        manuscriptSaved.getContent()
                    );
                    manuscriptList.setManuscriptStatus(manuscriptSaved.getStatus());
                    // ManuscriptList의 lastModifiedAt도 LocalDateTime이라고 가정
                    manuscriptList.setLastModifiedAt(
                        manuscriptSaved.getLastModifiedAt() // String.valueOf() 제거, 직접 할당
                    );
                    // view 레파지 토리에 save
                    manuscriptListRepository.save(manuscriptList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}