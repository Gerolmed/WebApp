package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to validate restaurants.
 */
public class RestaurantValidator implements Validator {

    @Autowired
    private javax.validation.Validator beanValidator;

    private Set<Validator> springValidators;

    public RestaurantValidator() {
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
        return true;
        //return Restaurant.class.isAssignableFrom(clazz);
    }

    /**
     * Validates the restaurant.
     * @param target the target restaurant
     * @param errors Error handling
     */
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
