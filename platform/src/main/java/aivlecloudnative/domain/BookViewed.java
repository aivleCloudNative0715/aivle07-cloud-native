package aivlecloudnative.domain;

import aivlecloudnative.domain.*;
import aivlecloudnative.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
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

    public BookViewed(Book aggregate) {
        super(aggregate);
    }

    public BookViewed() {
        super();
    }
}
//>>> DDD / Domain Event
