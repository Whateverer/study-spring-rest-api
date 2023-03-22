package me.whatever.common;

import me.whatever.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public static ErrorsResource of(Errors errors) {
        return (ErrorsResource) EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
