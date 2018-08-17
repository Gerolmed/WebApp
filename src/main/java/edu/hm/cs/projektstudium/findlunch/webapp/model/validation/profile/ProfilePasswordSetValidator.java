package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates the setting of a password.
 */

public class ProfilePasswordSetValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return ProfileForm.class.isAssignableFrom(clazz);
    }

    /**
     * Validates the password set.
     * @param target the target
     * @param errors the errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        ProfileForm profileForm = (ProfileForm) target;

        String validPassword = profileForm.getValidPassword();
        String newPassword = profileForm.getNewPassword();
        String newPasswordRepeat = profileForm.getRepeatNewPassword();

        //The validation is true if none of the 3 passwords fields is used or if every of the 3 passwords fields is used.
        if  ((validPassword.equals("") && newPassword.equals("") && newPasswordRepeat.equals("")) || (!validPassword.equals("") && !newPassword.equals("") && !newPasswordRepeat.equals(""))) {
            //empty
        } else {
            errors.reject("profile.validation.passwordSet");
        }
    }
}
