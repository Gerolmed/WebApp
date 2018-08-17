package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Interface for the unique profile email.
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ProfileEmailUniqueValidator.class)
@Documented
public @interface ProfileEmailUnique {

    String message() default "{profile.validation.emailUnique}";

    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}
