package aivlecloudnative.domain;

import aivlecloudnative.infra.AbstractEvent;
import lombok.*;

@Data
@ToString
public class BookViewed extends AbstractEvent {

    private Long userId;
    private Long bookId;

    private String title;
    private String authorName;
    private String summary;
    private String category;
    private String coverImageUrl;
    private String ebookUrl;
    private String subscriptionFee;
    private String viewCount;
}
