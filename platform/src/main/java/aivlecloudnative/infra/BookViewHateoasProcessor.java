package aivlecloudnative.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import aivlecloudnative.domain.BookView;
import org.springframework.lang.NonNull;

@Component
public class BookViewHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<BookView>> {

    @Override
    public @NonNull EntityModel<BookView> process(@NonNull EntityModel<BookView> model) {
        return model;
    }
}
