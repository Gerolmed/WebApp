package edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validates that the profile password is equal.
 */

public class ProfilePasswordEqualValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return ProfileForm.class.isAssignableFrom(clazz);
    }

    /**
     * Validates that the new and repeated password are equal.
     * @param target the target
     * @param errors the errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        ProfileForm profileForm = (ProfileForm) target;

        if(!profileForm.getNewPassword().equals(profileForm.getRepeatNewPassword())) {
            errors.rejectValue("newPassword", "universal.validation.passwordNotEqual");
        }

    }

}
