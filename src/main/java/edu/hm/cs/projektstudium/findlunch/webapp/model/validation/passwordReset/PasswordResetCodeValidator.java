package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordMapContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordRequestService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to validate the password reset code.
 */
public class PasswordResetCodeValidator implements ConstraintValidator<PasswordResetCode, String> {

    @Autowired
    private PasswordRequestService passwordRequestService;

    public void initialize(PasswordResetCode constraintAnnotation) {
        // intentionally left blank: this is the place to initialize the constraint annotation for any sensible default values.
    }

    /**
     * Validates the password code.
     * @param value the value
     * @param context the context
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {

        HashMap<String, PasswordMapContainer> resetCodes = passwordRequestService.getResetCodes();
        DateTime currentTime = DateTime.now();
        DateTime entryTime;

        for (Map.Entry<String, PasswordMapContainer> entry : resetCodes.entrySet()) {
            String savedCode = entry.getKey();
            entryTime = entry.getValue().getDateTime();

            if(savedCode.equals(value)) {

                //The security code is invalid after 2 min. (2min. = 120 000)
                if(entryTime.isAfter(currentTime.minusMinutes(2))) {
                    return true;
                }
            }
        }

        return false;
    }
}
