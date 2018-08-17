package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The class is responsible for handling http calls related to the login of the swa.
 */
@Controller
public class LoginControllerSWA {

    @RequestMapping(value = {"/swa/login", "/swa"}, method = RequestMethod.GET)
    public String login() {
        return "swa_login";
    }

}
