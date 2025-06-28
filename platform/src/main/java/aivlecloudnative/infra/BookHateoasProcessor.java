package aivlecloudnative.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import aivlecloudnative.domain.Book;

@Component
public class BookHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Book>> {

    @Override
    public @NonNull EntityModel<Book> process(@NonNull EntityModel<Book> model) {
        return model;
    }
}
