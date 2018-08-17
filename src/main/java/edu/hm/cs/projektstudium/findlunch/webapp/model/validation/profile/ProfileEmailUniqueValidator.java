package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * /**
 * Validates that the email of a profile is unique.
 * Created by Alexander Carl on 25.06.2017.
 */
public class ProfileEmailUniqueValidator implements ConstraintValidator<ProfileEmailUnique, String> {

    @Autowired
    private DbReaderService dbReaderService;

    @Override
    public void initialize(ProfileEmailUnique constraintAnnotation) {
        // intentionally left blank: this is the place to initialize the constraint annotation for any sensible default values.
    }

    /**
     * Validates the email of a profile.
     * @param value the value
     * @param context the context
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        SalesPerson salesPerson = dbReaderService.getSalesPersonByEmail(value);

        //The email is not used and therefore valid as a new email
        if(salesPerson == null) {
            return true;
        }

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        //A user wants to keep its own email
        if(salesPerson.getEmail().equals(loggedInUser)) {
            return true;
        }

        return false;

    }

}
