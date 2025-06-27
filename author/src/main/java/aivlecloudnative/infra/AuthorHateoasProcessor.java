package aivlecloudnative.infra;

import aivlecloudnative.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class AuthorHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Author>> {

    @Override
    public EntityModel<Author> process(EntityModel<Author> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/applyauthor")
                .withRel("applyauthor")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/judgeauthor")
                .withRel("judgeauthor")
        );

        return model;
    }
}
