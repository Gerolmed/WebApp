package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of the interface UserDetailsService.
 */
@Service
public class LoginServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbReaderService dbReaderService;

    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        SalesPerson salesPerson = dbReaderService.getSalesPersonByEmail(userEmail);

        if (salesPerson != null) {
            UserDetails userDetails = (UserDetails) new User(salesPerson.getEmail(), salesPerson.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_VMA")));
            logger.debug("Login - User: " + userEmail + " identified and logged in.");
            return  userDetails;
        } else {
            UserDetails userDetails = (UserDetails) new User(userEmail, "unknownEMail", Arrays.asList(new SimpleGrantedAuthority("ROLE_VMA")));
            logger.debug("Login failure - User: " + userEmail + " id or password unknown.");
            return userDetails;
            //The sha256-Code is always longer than the 11 chars of the "unknownEMail" String, therefore the else statement will never lead to a successful log in.
        }
    }

}
