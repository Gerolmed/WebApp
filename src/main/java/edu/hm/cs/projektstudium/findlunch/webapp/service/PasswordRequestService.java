package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordMapContainer;

import java.util.HashMap;

/**
 * Interface for services related to the password request service.
 */
public interface PasswordRequestService {

    String createPasswordResetCode(String userEmail);

    String getUserEmail(String resetCode);

    void deletePasswordResetCode(String resetCode);

    HashMap<String, PasswordMapContainer> getResetCodes();

}
