package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordRequest;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates unknown emails.
 */
public class EmailUnknownValidator implements ConstraintValidator<EmailUnknown, String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbReaderService dbReaderService;

    public void initialize(EmailUnknown constraintAnnotation) {
        // intentionally left blank; this is the place to initialize the constraint annotation for any logical default values.
    }

    /**
     * Checks if email of sales person is valid.
     * @param value Email of sales person
     * @param context the context
     * @return true, if email is valid
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {
        SalesPerson salesPerson;

        try {
            salesPerson = dbReaderService.getSalesPersonByEmail(value);
        } catch (Exception ignore) {
            logger.debug("PasswordRequestForm failure - NullPointerAcception - getSalesPersonByEmail(value) was executed with value = null.");
            salesPerson = null;
        }

        if(salesPerson == null) {
            logger.debug("PasswordRequestForm failure - User: " + value + " is not in the Database.");
            return false;
        }

        return true;
    }
}
