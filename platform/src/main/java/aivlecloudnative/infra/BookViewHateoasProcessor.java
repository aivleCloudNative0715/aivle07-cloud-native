package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookViewHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<BookView>> {

    @Override
    public EntityModel<BookView> process(EntityModel<BookView> model) {
        return model;
    }
}
