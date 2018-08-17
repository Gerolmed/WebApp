package edu.hm.cs.projektstudium.findlunch.webapp.service;

/**
 * Interface for services related to the password reset service.
 */
public interface PasswordResetService {

    void setNewPassword(String newPassword, String securityCode);

}
