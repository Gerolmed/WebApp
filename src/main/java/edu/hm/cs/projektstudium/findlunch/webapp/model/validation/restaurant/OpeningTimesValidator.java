package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant;

import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * Validates opening times of a restaurant.
 */
public class OpeningTimesValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        //return Restaurant.class.isAssignableFrom(clazz);
        return true;
    }

    /**
     * Validates the opening times.
     * @param target the target restaurant
     * @param errors Error handling if opening time is null
     */
    @Override
    public void validate(Object target, Errors errors) {
        Restaurant restaurant = (Restaurant) target;
        List<RestaurantTimeContainer> openingTimes = restaurant.getOpeningTimes();
        //Regex-Source: https://stackoverflow.com/questions/7536755/regular-expression-for-matching-hhmm-time-format
        String stringPattern = "(^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$)|"; //pattern for hh:mm
        boolean timeFormatValid = true;

        //Format check: "hh:mm"
        for (RestaurantTimeContainer openingTime : openingTimes) {
            String startTime = openingTime.getStartTime();
            String endTime = openingTime.getEndTime();

            if(!(startTime.matches(stringPattern) && endTime.matches(stringPattern))) {
                timeFormatValid = false;
            }
        }

        if(!timeFormatValid) {
            errors.rejectValue("openingTimes", "restaurant.validation.openingTimes");
        }
    }
}
