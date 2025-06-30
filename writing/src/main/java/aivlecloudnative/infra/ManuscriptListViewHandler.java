package aivlecloudnative.infra;

import aivlecloudnative.domain.ManuscriptList;
import aivlecloudnative.domain.ManuscriptRegisterd;
import aivlecloudnative.domain.ManuscriptSaved;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;
import java.util.function.Consumer;
import java.util.Optional; 

// 로깅을 위해 추가
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ManuscriptListViewHandler {

    // 로거 인스턴스 생성
    private static final Logger logger = LoggerFactory.getLogger(ManuscriptListViewHandler.class);

    @Autowired
    private ManuscriptListRepository manuscriptListRepository;

    // --- ManuscriptRegisterd 이벤트를 처리하는 Consumer Bean ---
    @Bean
    public Consumer<ManuscriptRegisterd> whenManuscriptRegisterd_then_CREATE_1() {
        return manuscriptRegisterd -> {
            try {
                // 이벤트 유효성 검증
                if (!manuscriptRegisterd.validate()) {
                    logger.warn("ManuscriptRegisterd event validation failed: {}", manuscriptRegisterd);
                    return;
                }

                logger.info("Handling ManuscriptRegisterd event for ID: {}", manuscriptRegisterd.getId());

                // Read Model(ManuscriptList) 객체 생성
                ManuscriptList manuscriptList = new ManuscriptList();
                
                manuscriptList.setManuscriptId(manuscriptRegisterd.getId()); // Manuscript의 ID를 Read Model의 필드에 저장
                manuscriptList.setManuscriptTitle(manuscriptRegisterd.getTitle());
                manuscriptList.setManuscriptContent(manuscriptRegisterd.getContent());
                manuscriptList.setManuscriptStatus(manuscriptRegisterd.getStatus());
                manuscriptList.setLastModifiedAt(manuscriptRegisterd.getLastModifiedAt());
                manuscriptList.setAuthorId(manuscriptRegisterd.getAuthorId()); // 'AuthorId'로 일관성 유지

                // Read Model Repository에 저장 (새로운 레코드 생성)
                manuscriptListRepository.save(manuscriptList);
                logger.info("Successfully created ManuscriptList entry for new manuscript ID: {}", manuscriptRegisterd.getId());

            } catch (Exception e) {
                // 실제 서비스에서는 e.printStackTrace() 대신 로깅 프레임워크 사용
                logger.error("Error processing ManuscriptRegisterd event for ID {}: {}", manuscriptRegisterd.getId(), e.getMessage(), e);
            }
        };
    }

    // --- ManuscriptSaved 이벤트를 처리하는 Consumer Bean ---
    // 원고가 저장되면 Read Model의 해당 원고 정보를 업데이트
    @Bean
    public Consumer<ManuscriptSaved> whenManuscriptSaved_then_UPDATE_1() {
        return manuscriptSaved -> {
            try {
                // 이벤트 유효성 검증
                if (!manuscriptSaved.validate()) {
                    logger.warn("ManuscriptSaved event validation failed: {}", manuscriptSaved);
                    return;
                }
                
                logger.info("Handling ManuscriptSaved event for manuscript ID: {}", manuscriptSaved.getId());

                // Read Model 객체 조회 (이벤트의 Manuscript ID를 사용하여 정확한 레코드를 찾음)
                Optional<ManuscriptList> optionalManuscriptList = manuscriptListRepository.findByManuscriptId(
                    manuscriptSaved.getId() // Manuscript의 ID로 찾음
                );

                if (optionalManuscriptList.isPresent()) {
                    ManuscriptList manuscriptList = optionalManuscriptList.get();
                    
                    // Read Model 객체에 이벤트의 최신 데이터로 업데이트
                    manuscriptList.setManuscriptTitle(manuscriptSaved.getTitle());
                    manuscriptList.setManuscriptContent(manuscriptSaved.getContent());
                    manuscriptList.setManuscriptStatus(manuscriptSaved.getStatus());
                    manuscriptList.setLastModifiedAt(manuscriptSaved.getLastModifiedAt());
                    // authorId는 변경되지 않는다고 가정.

                    // Read Model Repository에 저장 (기존 레코드 업데이트)
                    manuscriptListRepository.save(manuscriptList);
                    logger.info("Successfully updated ManuscriptList entry for manuscript ID: {}", manuscriptSaved.getId());
                } else {
                    logger.warn("ManuscriptList entry not found for ManuscriptSaved event ID: {}. Skipping update.", manuscriptSaved.getId());
                }

            } catch (Exception e) {
                logger.error("Error processing ManuscriptSaved event for ID {}: {}", manuscriptSaved.getId(), e.getMessage(), e);
            }
        };
    }

    
}