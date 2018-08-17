package edu.hm.cs.projektstudium.findlunch.webapp.components;

import org.joda.time.DateTime;

/**
 * This class is used in the PasswordRequestService HashMap.
 */
public class PasswordMapContainer {

    private String userEmail;
    private DateTime dateTime;

    public PasswordMapContainer(String userEmail, DateTime dateTime) {
        this.userEmail = userEmail;
        this.dateTime = dateTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

}
