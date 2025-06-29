package aivlecloudnative.infra;

import aivlecloudnative.domain.SubscriberSignedUp;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserEventHandler {

    private final AbstractEventPublisher eventPublisher;

    public UserEventHandler(AbstractEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener
    public void handle(SubscriberSignedUp event) {
        eventPublisher.publish("event-out", event, "SubscriberSignedUp");
    }
}
