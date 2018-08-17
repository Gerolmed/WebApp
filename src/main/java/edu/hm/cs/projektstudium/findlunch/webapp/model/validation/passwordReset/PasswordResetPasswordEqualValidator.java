package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordResetForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates that the password reset passwords are equal.
 */

@Component
public class PasswordResetPasswordEqualValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return PasswordResetForm.class.isAssignableFrom(clazz);
    }

    /**
     * Validates that the new and repeated password in the reset form are equal.
     * @param target the target
     * @param errors the errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        PasswordResetForm passwordResetForm = (PasswordResetForm) target;

        if(!passwordResetForm.getNewPassword().equals(passwordResetForm.getNewPasswordRepeat())) {
            errors.rejectValue("newPassword", "universal.validation.passwordNotEqual");
        }
    }

}
