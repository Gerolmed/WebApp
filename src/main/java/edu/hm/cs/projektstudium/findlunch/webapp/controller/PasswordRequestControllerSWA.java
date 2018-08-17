package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordRequestForm;
import edu.hm.cs.projektstudium.findlunch.webapp.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * The class is responsible for handling http calls related to the password reset request of the swa.
 * Created by Alexander Carl on 18.06.2017.
 */
@Controller
public class PasswordRequestControllerSWA {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailService emailService;

    /**
     * Sends a request to initiate a password reset.
     * @param model Model in which necessary object are placed to be displayed on the website
     * @return Password request
     */
    @RequestMapping(value = "/swa/passwordRequest", method = RequestMethod.GET)
    public String passwordRequest(Model model) {
        PasswordRequestForm passwordRequestForm = new PasswordRequestForm();
        model.addAttribute("passwordRequestForm", passwordRequestForm);

        return "swa_passwordRequest";
    }

    /**
     * Processes the password request.
     * @param passwordRequestForm Form to request a password reset
     * @param bindingResult Binding result
     * @return Redirects to the password reset page.
     */
    @RequestMapping(value = "/swa/passwordRequest", method = RequestMethod.POST)
    public String processPasswordRequest(@ModelAttribute("passwordRequestForm") @Valid PasswordRequestForm passwordRequestForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "swa_passwordRequest";
        }
        emailService.generatePasswordRequestMail(passwordRequestForm.getEmail());

        if(emailService.sendMail() == true) {
            return "redirect:/swa/passwordReset";
        } else {
            logger.debug("PasswordRequestMail (User: " + passwordRequestForm.getEmail() + ") has been send.");
            return "redirect:/swa/login?mailAccountTakesToLongToAnswer";
        }
    }
}
