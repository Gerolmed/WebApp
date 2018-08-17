package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that the entered profile password is correct.
 */
public class ProfilPasswordValidator implements ConstraintValidator<ProfilPassword, String> {

    @Autowired
    private DbReaderService dbReaderService;

    private ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);

    @Override
    public void initialize(ProfilPassword constraintAnnotation) {
        // intentionally left blank: this is the place to initialize the constraint annotation for any sensible default values.
    }

    /**
     * Validates the password of a profile.
     * @param value the value
     * @param context the context
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String encodedPassword = encoder.encodePassword(value, null);

        //If the user doesn`t enter a password, the old one remains. Saving of the new password is handled by the controller.
        if (value.equals(""))
            return true;

        //Checks if the user entered the correct password
        if(encodedPassword.equals(dbReaderService.getSalesPersonByEmail(loggedInUser).getPassword())) {
            return true;
        }

        return false;
    }
}
