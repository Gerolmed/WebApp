package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates the kitchen type.
 */
public class KitchenTypeValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        //return Restaurant.class.isAssignableFrom(aClass);
        return true;
    }

    /**
     * Validates the kitchen type of a restaurant.
     * @param target the target restaurant
     * @param errors Error handling if kitchenType is null
     */
    @Override
    public void validate(Object target, Errors errors) {
        Restaurant restaurant = (Restaurant) target;

        Object kitchenType = null;

        try {
            kitchenType = restaurant.getKitchenTypesAsString().get(0);
        } catch (Exception e) {
            //kitchenTypesAsString is empty
        }

        if(kitchenType == null) {
            errors.rejectValue("offerTimes", "restaurant.validation.kitchenTypes");
        }
    }
}
