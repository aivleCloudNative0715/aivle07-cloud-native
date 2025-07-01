package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

@Component
public class PublicationStatusViewHandler {

    private final PublicationStatusRepository publicationStatusRepository;

    public PublicationStatusViewHandler(PublicationStatusRepository publicationStatusRepository) {
        this.publicationStatusRepository = publicationStatusRepository;
    }

    // 1. PublicationInfoCreationRequested 이벤트 수신
    @org.springframework.context.annotation.Bean
    public Consumer<PublicationInfoCreationRequested> publicationInfoCreationRequestedHandler() {
        return publicationInfoCreationRequested -> {
            try {
                if (!publicationInfoCreationRequested.validate()) return;
                PublicationStatus publicationStatus = new PublicationStatus();
                publicationStatus.setManuscriptId(publicationInfoCreationRequested.getManuscriptId());
                publicationStatus.setStatus(publicationInfoCreationRequested.getStatus());
                publicationStatusRepository.save(publicationStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // 2. AutoPublished 이벤트 수신
    @org.springframework.context.annotation.Bean
    public Consumer<AutoPublished> autoPublishedHandler() {
        return autoPublished -> {
            try {
                if (!autoPublished.validate()) return;
                List<PublicationStatus> publicationStatusList =
                        publicationStatusRepository.findByManuscriptId(autoPublished.getManuscriptId());
                for (PublicationStatus publicationStatus : publicationStatusList) {
                    publicationStatus.setStatus(autoPublished.getStatus());
                    publicationStatusRepository.save(publicationStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
