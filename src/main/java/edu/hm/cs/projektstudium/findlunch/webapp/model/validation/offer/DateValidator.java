package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates the date of an offer.
 *
 */
public class DateValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Offer.class.isAssignableFrom(aClass);
    }

    /**
     * Validates the date.
     * @param target the target
     * @param errors the errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        Offer offer = (Offer) target;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-M-yyyy");

        if(!offer.getStartDateAsString().equals("") && !offer.getEndDateAsString().equals("")) {
            DateTime startDate = null;
            DateTime endDate = null;

            try {
                startDate = formatter.parseDateTime(offer.getStartDateAsString());
            } catch (Exception e) {
                //The regex pattern is not 100% safe
            }

            try {
                endDate = formatter.parseDateTime(offer.getEndDateAsString());
            } catch (Exception e) {
                //The regex pattern is not 100% safe
            }

            if(startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    errors.reject("offer.validation.timeWindow");
                }
            }
        }
    }
}
