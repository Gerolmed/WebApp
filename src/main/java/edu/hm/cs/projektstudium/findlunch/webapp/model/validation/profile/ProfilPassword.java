package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

/**
 * Inferface for the profile password.
 */

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ProfilPasswordValidator.class)
@Documented
public @interface ProfilPassword {

    String message() default "{universal.validation.noValidPassword}";

    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}