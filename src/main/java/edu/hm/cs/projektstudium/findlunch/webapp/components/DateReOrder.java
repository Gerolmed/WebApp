package edu.hm.cs.projektstudium.findlunch.webapp.components;

/**
 * This class is used to re-order dates.
 * It re-orders dates or date-time input given as string.
 */
public class DateReOrder {

    public String reOrderDateString(String dateAsString) {
        if(dateAsString.length() != 10) { //10
            return dateAsString;
        } else {
            String year = dateAsString.substring(0,4);
            String month = dateAsString.substring(5,7);
            String day = dateAsString.substring(8,10);

            return day + "-" + month + "-" + year;
        }
    }

    /**
     * Re-orders given input date
     * @param dateAsString Input date in string form
     * @return Unmodified input if string is not 16 characters long or date re-ordered by reOrderDateString method and time
     */
    public String reOrderDateTimeString(String dateAsString) {
        if(dateAsString.length() != 16) { //16
            return dateAsString;
        } else {
            String date = dateAsString.substring(0,10);
            date = reOrderDateString(date);
            String time = dateAsString.substring(11,16);

            return date + " " + time;
        }
    }
}
