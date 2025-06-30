package aivlecloudnative.infra;

import aivlecloudnative.domain.Manuscript; 
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.lang.NonNull; 
import org.springframework.stereotype.Component;

@Component
public class ManuscriptHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Manuscript>> {

    @Override
    public @NonNull EntityModel<Manuscript> process(@NonNull EntityModel<Manuscript> model) {
        return model;
    }
}