package edu.hm.cs.projektstudium.findlunch.webapp.components;

import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordRequest.EmailUnknown;

import javax.validation.constraints.Pattern;

/**
 * Form to request a forgotten password with e-mail.
 */
public class PasswordRequestForm {

    @Pattern(regexp = ".+@.+\\..+", message = "{universal.validation.pattern.email}")
    @EmailUnknown
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
