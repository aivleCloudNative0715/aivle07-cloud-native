package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookWorkHateoasProcessor
        implements RepresentationModelProcessor<EntityModel<BookWork>> {

    @Override
    public EntityModel<BookWork> process(EntityModel<BookWork> model) {
        return model;
    }
}
