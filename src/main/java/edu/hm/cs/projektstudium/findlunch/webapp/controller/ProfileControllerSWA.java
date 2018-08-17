package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfileValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.service.CountryService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Controller for the dialog to User-Profile-Settings
 */
@Controller
public class ProfileControllerSWA {

    @Autowired
    private CountryService countryService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileValidator profileValidator;

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The user profile.
     * @param model Model in which necessary object are placed to be displayed on the website
     * @return The user profile
     */
    @RequestMapping(value = "/swa/profile", method = RequestMethod.GET)
    public String profile(Model model) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        SalesPerson salesPerson = profileService.getSalesByEmail(loggedInUser);
        profileService.addSalesPersonToTransactionStore(salesPerson);

        model.addAttribute("profileForm", new ProfileForm(salesPerson));
        model.addAttribute("countries", countryService.getAllCountries());

        return "swa_profile";
    }

    /**
     * Saves the profile if no errors occure.
     * @param model Model in which necessary object are placed to be displayed on the website
     * @param profileForm The profile form
     * @param bindingResult The binding result
     * @return The profile
     */
    @RequestMapping(value = "/swa/saveProfile", method = RequestMethod.POST)
    public String saveProfile(Model model, @Valid ProfileForm profileForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //The attributes must be added again to the profile. This can also be done using the @ModelAttribute Annotation
            model.addAttribute("profileForm", profileForm);
            model.addAttribute("countries", countryService.getAllCountries());

            return "swa_profile";
        }

        //Security check if the concerning the DB-Object salesPerson has been altered during transaction.
        if (profileService.salesPersonHasBeenAlteredMeanwhile(profileForm.getId())) {
            return "redirect:/home?profileWasChangedMeanwhile";
        } else {
            if (profileService.emailOfSalesPersonHasBeenAltered(profileForm)) {
                profileService.saveSalesPerson(profileForm);
                profileService.setNewPassword(profileForm);
                logger.debug("User (User-ID: " + profileForm.getId() + ") logged out successfully.");
                SecurityContextHolder.getContext().setAuthentication(null);
                return "redirect:/login?profileEmailWasChanged";
            } else {
                profileService.saveSalesPerson(profileForm);
                profileService.setNewPassword(profileForm);
                return "redirect:/swa/home?profileChangeSuccess";
            }
        }
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setValidator(profileValidator);
    }

}
