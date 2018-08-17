package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordRequest;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Interface to handle unknown emails.
 */
@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = EmailUnknownValidator.class)
@Documented
public @interface EmailUnknown {

    String message() default "{universal.validation.emailUnknown}";

    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}
