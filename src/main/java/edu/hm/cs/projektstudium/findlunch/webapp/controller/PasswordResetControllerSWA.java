package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordResetForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.passwordReset.PasswordResetValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * The class is responsible for handling http calls related to the password reset of the swa.
 */
@Controller
public class PasswordResetControllerSWA {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordResetValidator passwordResetValidator;

    /**
     * Initiates the password reset.
     * @param model Model in which necessary object are placed to be displayed on the website
     * @return The passwort reset method.
     */
    @RequestMapping(value = "/swa/passwordReset", method = RequestMethod.GET)
    public String passwordReset(Model model) {
        PasswordResetForm passwordResetForm = new PasswordResetForm();
        model.addAttribute("passwordResetForm", passwordResetForm);

        return "swa_passwordReset";
    }

    /**
     * Processes the password reset.
     * @param passwordResetForm Form to reset the password
     * @param bindingResult The binding result
     * @return Redirects to the passwordChangeSuccess site if successful
     */
    @RequestMapping(value = "/swa/passwordReset", method = RequestMethod.POST)
    public String processPasswordReset(@ModelAttribute("passwordResetForm") @Valid PasswordResetForm passwordResetForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "swa_passwordReset";
        }
        passwordResetService.setNewPassword(passwordResetForm.getNewPassword(), passwordResetForm.getSecurityCode());

        return "redirect:/swa/login?passwordChangeSuccess";
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setValidator(passwordResetValidator);
    }

}
