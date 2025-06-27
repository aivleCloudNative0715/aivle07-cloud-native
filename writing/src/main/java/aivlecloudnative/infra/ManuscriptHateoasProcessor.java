package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class ManuscriptHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Manuscript>> {

    @Override
    public EntityModel<Manuscript> process(EntityModel<Manuscript> model) {
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() +
                    "/publicationrequest"
                )
                .withRel("publicationrequest")
        );
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() +
                    "/manuscriptregistration"
                )
                .withRel("manuscriptregistration")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/manuscriptsave")
                .withRel("manuscriptsave")
        );

        return model;
    }
}
