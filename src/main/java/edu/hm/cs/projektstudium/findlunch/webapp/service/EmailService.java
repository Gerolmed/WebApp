package edu.hm.cs.projektstudium.findlunch.webapp.service;

/**
 * Interface for services related to the email service.
 */
public interface EmailService {

    void generatePasswordRequestMail(String userEmail);

    boolean sendMail();

}
