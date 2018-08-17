package edu.hm.cs.projektstudium.findlunch.webapp.components;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Container which contains information about opening and closing time for each day.
 */
@Getter
@Setter
public class RestaurantTimeContainer {

    private String startTime;
    private String endTime;
    private int dayNumber; //0=Monday
    private String dayNumberAsString; //Needed for the dialog "offer"

    public RestaurantTimeContainer() {
        super();
    }

    /**
     * Contains opening and closing time for a day.
     * @param startTime opening time
     * @param endTime closing time
     * @param dayNumber number of week day
     */
    public RestaurantTimeContainer(Date startTime, Date endTime, int dayNumber) {
        if(!(startTime == null)) {
            this.startTime = new SimpleDateFormat("HH:mm").format(startTime);
        } else {
            this.startTime = null;
        }

        if(!(endTime == null)) {
            this.endTime = new SimpleDateFormat("HH:mm").format(endTime);
        } else {
            this.endTime = null;
        }

        this.dayNumber = dayNumber;
        this.dayNumberAsString = String.valueOf(dayNumber);
    }

}
