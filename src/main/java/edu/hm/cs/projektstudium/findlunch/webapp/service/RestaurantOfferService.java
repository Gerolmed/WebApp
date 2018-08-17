package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OpeningTime;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.model.comparison.RestaurantDistanceComparator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service-Klasse für die Restaurant- und Offer-Rest-Controller.
 * Führt Abfragen auf den Repositories aus und gibt die Ergebnisse in Form von Listen/Maps zurück.
 */
@Service
public class RestaurantOfferService {
    /** The offer repository. */
    private final OfferRepository offerRepo;

    /** The restaurant repository. */
    private final RestaurantRepository restaurantRepo;

    @Autowired
    public RestaurantOfferService(RestaurantRepository restaurantRepo, OfferRepository offerRepo) {
        this.restaurantRepo = restaurantRepo;
        this.offerRepo = offerRepo;
    }

    /**
     * Gibt Angebote in einem bestimmten Radius mit dem dazugehörigen Restaurant aus, die aktuell geöffnet haben.
     *
     * @param longitude Längengrad
     * @param latitude Breitengrad
     * @param restaurantNumber Anzahl der zu suchenden Restaurants
     * @param offerNumber Anzahl der Angebote
     * @param currentlyOpen Muss das Restaurant geöffnet sein?
     * @param calendar Zeitpunkt, für den gesucht werden soll.
     * @return LinkedHashMap mit Restaurant, Angebotsliste.
     */
    public List<Offer> getOffersToLocation(float longitude, float latitude, int restaurantNumber, int offerNumber, boolean currentlyOpen, Calendar calendar/*, String username*/) {

        int radius = 2000;

        List<Offer> offers = new ArrayList();
        List<Restaurant> restaurants;

        restaurants = getAllRestaurants(longitude, latitude, radius, restaurantNumber, currentlyOpen);
        for (Restaurant restaurant : restaurants) {
            // check if restaurant has a TimeSchedule for today
            TimeSchedule ts = restaurant.getTimeSchedules().stream().filter(item -> item.getDayOfWeek().getDayNumber() == calendar.get(Calendar.DAY_OF_WEEK)).findFirst().orElse(null);
            // only get , that are valid at the moment
            List<Offer> tempOffers = getValidOffers(calendar,ts,restaurant.getId(),offerNumber);
            if (ts != null && tempOffers != null) {
                offers.addAll(tempOffers);
            }
            //Nur so viele Angebote wie gewünscht auslesen.
            if((offerNumber*restaurantNumber) <= offers.size()){break;}
        }
        return offers;
    }

    /**
     * Gets the restaurants within a given radius. Ordered by service (ascending)
     *
     * @param longitude
     *            the longitude used to get the center for the radius
     *            calculation
     * @param latitude
     *            the latitude used to get the center for the radius calculation
     * @param radius
     *            the radius within the restaurants should be located
     * @return the restaurants within the given radius. The flag "isFavorite" is
     *         always false
     */
    public List<Restaurant> getAllRestaurants (float longitude, float latitude, int radius){
        return getAllRestaurants(longitude, latitude, radius, 100, false);
    }

    /**
     * Gets the nearest restaurants within a given radius.
     * Ordered by service (ascending) and limited by restaurantNumber.
     *
     * @param longitude
     *            the longitude used to get the center for the radius
     *            calculation
     * @param latitude
     *            the latitude used to get the center for the radius calculation
     * @param radius
     *            the radius within the restaurants should be located
     * @param restaurantNumber number of restaurants to be returned
     * @param currentlyOpen If true, only currently open restaurants will be returned.
     * @return the restaurants within the given radius. The flag "isFavorite" is
     *         always false
     */
    private List<Restaurant> getAllRestaurants(float longitude, float latitude, int radius, int restaurantNumber, boolean currentlyOpen) {
        List<Restaurant> restaurantList = restaurantRepo.findAllByBlockedIsFalse();

        // Distanz zur übergebenen Position berechnen und im Restaurant-Modell speichern.
        for (int i = 0; i < restaurantList.size(); i++) {
            Restaurant currentRestaurant = restaurantList.get(i);
            currentRestaurant.setDistance(DistanceCalculator.calculateDistance(latitude, longitude,
                    currentRestaurant.getLocationLatitude(), currentRestaurant.getLocationLongitude()));

            // remove restaurants which are located outside the radius around
            // the user location.
            if (currentRestaurant.getDistance() > radius) {
                restaurantList.remove(i);
                i--;
            }
        }

        // sort by service (ascending)
        restaurantList.sort(new RestaurantDistanceComparator());

        // If the restaurant has no specific openingtimes it has to be set on the offertime
        Iterator<Restaurant> i = restaurantList.iterator();
        while (i.hasNext()) {
            Restaurant restaurant = i.next();
            for(TimeSchedule schedule : restaurant.getTimeSchedules()) {
                if(schedule.getOpeningTimes().isEmpty()) {
                    List<OpeningTime> openingTimes = new ArrayList<>();
                    OpeningTime open = new OpeningTime();

                    open.setTimeSchedule(schedule);
                    open.setOpeningTime(schedule.getOfferStartTime());
                    open.setClosingTime(schedule.getOfferEndTime());
                    openingTimes.add(open);
                    schedule.setOpeningTimes(openingTimes);
                }
            }
            // Wenn nur offene Restaurants berücksichtigt werden sollen, nicht geöffnetes entfernen.
            if(currentlyOpen) {
                if(restaurant.getCurrentlyOpen()) {
                    i.remove();
                }
            }
        }
        // Eine Sub-Liste aller Restaurants ausgeben mit max. "restaurantNumber" Elementen.
        return restaurantList.subList(0, Math.min(restaurantNumber, restaurantList.size()));
    }

    /**
     * Gets all valid offers of a restaurant.
     *
     * @param c
     *            the Calendar with the day and time to check (preferred: now)
     * @param ts
     *            the TimeSchedule that has to be checked
     * @param restaurantId
     *            the id of the Restaurant
     * @return the result, where the valid offers should be stored
     */
    public List<Offer> getValidOffers(Calendar c, TimeSchedule ts, int restaurantId) {
        return getValidOffers(c, ts, restaurantId, 100);
    }

    /**
     * Gets the valid offers of a restaurant.
     *
     * @param c
     *            the Calendar with the day and time to check (preferred: now)
     * @param ts
     *            the TimeSchedule that has to be checked
     * @param restaurantId
     *            the id of the Restaurant
     * @param offerNumber Anzahl an Angeboten
     * @return the result, where the valid offers should be stored
     */
    private List<Offer> getValidOffers(Calendar c, TimeSchedule ts, int restaurantId, int offerNumber) {

        List<Offer> result;

        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMin = c.get(Calendar.MINUTE);
        int currentTime = currentHour * 60 + currentMin;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(ts.getOfferStartTime());
        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int startMin = startCalendar.get(Calendar.MINUTE);
        int startTime = startHour * 60 + startMin;

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(ts.getOfferEndTime());
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        int endMin = endCalendar.get(Calendar.MINUTE);
        int endTime = endHour * 60 + endMin;

        if (startTime <= currentTime && endTime >= currentTime) {
            Date today = getZeroTimeDate(new Date());

            result = new ArrayList<>();

            for (Offer o : offerRepo.findByRestaurant_idOrderByOrderAsc(restaurantId)) {
                Date startDate = getZeroTimeDate(o.getStartDate());
                Date endDate = getZeroTimeDate(o.getEndDate());

                if (today.equals(startDate) || today.equals(endDate)
                        || (today.after(startDate) && today.before(endDate))) {

                    if (o.getDayOfWeeks().stream().filter(item -> item.getDayNumber() == c.get(Calendar.DAY_OF_WEEK))
                            .findFirst().orElse(null) != null) {
                        // Wenn das Angebot nicht ausverkauft ist, zur Liste hinzufügen.
                        if(!o.isSoldOut()) {
                            result.add(o);
                        }
                    }
                }
                if(offerNumber <= result.size()){break;}
            }
            return result.isEmpty() ? null : result;
        }
        return null;
    }

    /**
     * Removes the time (hour, minute, second, millisecond) of a given date and
     * returns the time value
     *
     * @param date
     *            the date, where the time should be set to zero
     * @return the time value of the date, where the time was set to zero
     */
    private static Date getZeroTimeDate(Date date) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}