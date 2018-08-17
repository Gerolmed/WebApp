package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Class to validate offers.
 */
public class OfferValidator implements Validator {


    @Autowired
    private javax.validation.Validator beanValidator;

    private Set<Validator> springValidators;

    /**
     * Used in the WebAppContextConfig to add classic Spring Validators.
     * @param springValidators the spring validator
     */
    public void setSpringValidators(Set<Validator> springValidators) {
        this.springValidators = springValidators;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Offer.class.isAssignableFrom(aClass);
    }

    /**
     * Validates an offer.
     * @param target the target
     * @param errors the errors
     */
    @Override
    public void validate(Object target, Errors errors) {

        Set<ConstraintViolation<Object>> constraintViolations = beanValidator.validate(target);

        //Handles all violations of the Bean-Validation
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }

        //Handles all violations for the classic Spring Validators
        for (Validator validator : springValidators) {
            validator.validate(target, errors);
        }
    }
}
