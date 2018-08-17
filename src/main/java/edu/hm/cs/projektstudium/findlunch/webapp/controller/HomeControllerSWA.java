package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The class is responsible for handling http calls related to the /home page of the swa.
 */
@Controller
public class HomeControllerSWA {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HomeService homeService;

    /**
     * The /swa/home page.
     * @param model Model in which necessary object are placed to be displayed on the website.
     * @return The home page
     */
    @RequestMapping(value = "/swa/home")
    public String home(Model model) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("restaurants", homeService.createHomeRestaurantFormListForSalesPerson(loggedInUser));
        model.addAttribute("toDos", homeService.getAllToDosForSalesPerson(loggedInUser));
        model.addAttribute("currentPayment", homeService.getCurrentPaymentOfSalesPerson(loggedInUser));

        return "swa_home";
    }

    /**
     * The /swa/home/logout page.
     * @return The logout page.
     */
    @RequestMapping(value = "/swa/home/logout")
    public String logout() {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Logout - User: " + loggedInUser + " logged out.");
        SecurityContextHolder.getContext().setAuthentication(null);

        return "redirect:/swa/login?logout";
    }

    /**
     * Removes a toDo object.
     * @param toDoId Id of the toDo object.
     * @return Redirects to "redirect:/swa/home"
     */
    @RequestMapping(value = "/swa/home/remove")
    public String removeToDo(@RequestParam("toDo") int toDoId) {

        //Checks if the todo exist`s and the user has access to it. (security check, if the call parameter has been altered manually)
        if(!homeService.toDoAssignedToSalePerson(toDoId)) {
            return "redirect:/swa/home?noValidAccessToToDo";
        }
        homeService.deleteToDo(toDoId);

        return "redirect:/swa/home";
    }

}
