package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class BookViewed extends AbstractEvent {

    private Long id;
    private String title;
    private String authorName;
    private String summary;
    private String category;
    private String coverImageUrl;
    private String ebookUrl;
    private String subscriptionFee;
    private String viewCount;
}
