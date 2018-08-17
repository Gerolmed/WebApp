package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordResetForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates the password reset.
 */
public class PasswordResetValidator  implements Validator {

    @Autowired
    private javax.validation.Validator beanValidator;

    private Set<Validator> springValidators;

    public PasswordResetValidator() {
        springValidators = new HashSet<Validator>();
    }

    /**
     * Used in the WebAppContextConfig to add classic Spring Validators.
     * @param springValidators the spring validator
     */
    public void setSpringValidators(Set<Validator> springValidators) {
        this.springValidators = springValidators;
    }

    public boolean supports(Class<?> clazz) {
        return PasswordResetForm.class.isAssignableFrom(clazz);
    }

    /**
     * Sets the validator.
     * @param target the target
     * @param errors the errors
     */
    public void validate(Object target, Errors errors) {
        Set<ConstraintViolation<Object>> constraintViolations = beanValidator.validate(target);

        //Handles all violations of the Bean-Validation
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }

        //Handles all violations for the classic Spring Validatiors
        for(Validator validator: springValidators) {
            validator.validate(target, errors);
        }
    }

}
