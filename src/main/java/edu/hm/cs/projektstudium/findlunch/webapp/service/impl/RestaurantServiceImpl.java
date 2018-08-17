package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantAddCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantDeleteCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantListForm;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbWriterService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of the interface RestaurantService.
 */
@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private DbWriterService dbWriterService;

    //The restaurant`s id is the key.
    /**This Map is used to store the restaurant(data) which is send to the user.
    * When the user presses save, the restaurant(data) is loaded again from the DB und compared to the
    * restaurant(data) at the start (stored in the HashMap). If the restaurant(data) has been altered
    * on the server while the user worked on it, the save request is rejected. This logic
    * is used to ensure data consistency.
    * The word transaction is used twice here:
    * 1) Transaction: Start by the GET.Request for the restaurant model - End by the comparison check if the DB-Object has been altered.
    * 2) Transaction: Only used to save the restaurant(data) with @Transactional */
    private static HashMap<Integer, Restaurant> restaurantTransactionStore = new HashMap<>();

    @Override
    public List<RestaurantListForm> getAllRestaurantNamesForSalesPerson(String email) {

        List<Restaurant> restaurantList = dbReaderService.getAllRestaurantsOfSalesPerson(email);
        List<RestaurantListForm> restaurantListForms = new ArrayList<>();

        for (Restaurant restaurant : restaurantList) {
            restaurantListForms.add(new RestaurantListForm(restaurant));
        }

        return restaurantListForms;
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        return dbReaderService.getRestaurantById(id);
    }

    //Ensures that a salesPerson can only see his Restaurants. Also if he changes the call parameter manually.
    @Override
    public boolean restaurantAssignedToSalesPerson(int restaurantId) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String salesPersonOfRestaurant;

        try {
            salesPersonOfRestaurant = dbReaderService.getRestaurantById(restaurantId).getSalesPerson().getEmail();
        } catch (NullPointerException e) {
            logger.debug("Error - User: " + loggedInUser + " - There is no restaurant with the requested id: " + restaurantId + " in the DB.");
            //The user will see the alert-danger box which tells him that he has no access to the restaurant with this (faulty) restaurantID.
            return false;
        }

        if(loggedInUser.equals(salesPersonOfRestaurant)) {
            return true;
        }
        logger.debug("Error - User: " + loggedInUser + " - Tryed to access a Restaurant (RestaurantId: " + restaurantId + ") which he is not assigned to.");
        return false;
    }

    /**
    The way the customerID is created here is deliberately done different than in the findLunchApp.
    The id in the findLunchApp is created in an deprecated way and not checked for uniqueness.
     */
    @Override
    public int getUniqueCustomerId() {
        Random random = new Random();
        Restaurant restaurant = null;
        Integer newCustomerId = null;
        boolean unique = false;

        while(unique == false) {
            newCustomerId = 100000000 + random.nextInt(900000000);
            try {
                restaurant = dbReaderService.getRestaurantByCustomerId(newCustomerId);
            } catch (NullPointerException e) {
                //The new CustomerId is unique.
            }

            if (restaurant == null) {
                unique = true;
            }
        }

        return newCustomerId;
    }

    @Override
    public void addCategoryToRestaurant(RestaurantAddCategory restaurantAddCategory) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        //Security check to ensure that a salesPerson can only add categories to restaurants he is assigned to.
        Restaurant restaurant = dbReaderService.getRestaurantById(restaurantAddCategory.getRestaurantId());
        if(!restaurant.getSalesPerson().getEmail().equals(loggedInUser)) {
            logger.debug("Security Violation in the class RestaurantServiceImpl - user: " + loggedInUser + " tryed to add categories to a restaurant he is not assigned to.");

        } else {
            //Check if the String contains multiple course types. e.g. Vorspeise, Rotwein, Hauswein
            String fullString = restaurantAddCategory.getName();
            fullString = fullString.replaceAll("\\s+", ""); //deletes whitespaces
            String[] singleCourseTypesStrings = fullString.split("\\,"); //cuts the String after every ","
            List<String> courseTypesToAdd = new ArrayList<>();

            for (String subString : singleCourseTypesStrings) {
                if(!subString.equals(""))
                courseTypesToAdd.add(subString);
            }

            dbWriterService.addCategoryToRestaurant(courseTypesToAdd, restaurantAddCategory.getRestaurantId());
        }
    }

    @Override
    public void deleteCategoryFromRestaurant(RestaurantDeleteCategory restaurantDeleteCategory) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        //Security check to ensure that a salesPerson can only add categories to restaurants he is assigned to.
        Restaurant restaurant = dbReaderService.getRestaurantById(restaurantDeleteCategory.getRestaurantId());
        if(!restaurant.getSalesPerson().getEmail().equals(loggedInUser)) {
            logger.debug("Security Violation in the class RestaurantServiceImpl - user: " + loggedInUser + " tryed to delete categories from a restaurant he is not assigned to.");

        } else {
            //Check if the String contains multiple course types. e.g. Vorspeise, Rotwein, Hauswein
            String fullString = restaurantDeleteCategory.getName();
            fullString = fullString.replaceAll("\\s+",""); //deletes whitespaces
            String[] singleCourseTypesString = fullString.split("\\,"); //cuts the String after every ","
            List<CourseType> courseTypeList = dbReaderService.getRestaurantById(restaurantDeleteCategory.getRestaurantId()).getCourseTypeList();
            List<CourseType> courseTypesWithItemsToDelete = new ArrayList<CourseType>();

            for (String subString : singleCourseTypesString) {
                for (CourseType courseType : courseTypeList) {
                    if(subString.equals(courseType.getName())) {
                        dbWriterService.deleteCategoryFromRestaurant(courseType);
                    }
                }
            }
        }
    }

    @Override
    public void addRestaurantToRestaurantTransactionStore(Restaurant restaurant) {
        restaurantTransactionStore.put(restaurant.getId(), restaurant);
    }

    @Override
    // Security check if the concerning the DB-Object restaurant has been altered during transaction.
    public boolean restaurantHasBeenAlteredMeanwhile(Restaurant restaurant) {
        Restaurant restaurantTransactionEnd = dbReaderService.getRestaurantById(restaurant.getId());
        Restaurant restaurantTransactionStart = restaurantTransactionStore.get(restaurant.getId());

        if (!restaurantTransactionEnd.equals(restaurantTransactionStart)) {
            return true;
        }

        restaurantTransactionStore.remove(restaurant.getId());
        return false;
    }

    @Override
    public void saveRestaurant(Restaurant restaurant) {

        dbWriterService.saveRestaurant(restaurant);
    }

    @Override
    //Creates the QRCode like in the findlunchApp but without saving it temporarily as a file on the hard drive.
    public byte[] createQRCode(String qrUuid) {

        //Creates a bitMatrix for the given String qrUuid
        Map<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(qrUuid, BarcodeFormat.QR_CODE, 250, 250, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //Writes the BitMatrix to a (byte[])image
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public List<RestaurantType> getAllRestaurantTypes() {
        return dbReaderService.getAllRestaurantTypes();
    }

    @Override
    public List<KitchenType> getAllKitchenTypes() {
        return dbReaderService.getAllKitchenTypes();
    }

    @Override
    //The Html-Page does need the day numbers to be rendered correctly.
    public List<RestaurantTimeContainer> populateRestaurantTimeDayNumber() {
        List<RestaurantTimeContainer> times = new ArrayList<RestaurantTimeContainer>();

        for (int i = 1; i < 8; i++) {
            RestaurantTimeContainer restaurantTimeContainer = new RestaurantTimeContainer();
            restaurantTimeContainer.setDayNumber(i);
            times.add(restaurantTimeContainer);
        }

        return times;
    }

    @Override
    public List<CourseType> getAllCourseTypesOfRestaurant(int restaurantId) {
        return dbReaderService.getAllCourseTypesOfRestaurant(restaurantId);
    }

    @Override
    public Restaurant setDayNumbers(Restaurant restaurant) {
        List<RestaurantTimeContainer> openingTimes = restaurant.getOpeningTimes();
        List<RestaurantTimeContainer> offerTimes = restaurant.getOfferTimes();

        for(int i = 0; i < 7; i++) {
            int dayNumber = i + 1;
            openingTimes.get(i).setDayNumber(dayNumber);
            openingTimes.get(i).setDayNumberAsString(String.valueOf(dayNumber));
            offerTimes.get(i).setDayNumber(dayNumber);
            offerTimes.get(i).setDayNumberAsString(String.valueOf(dayNumber));
        }

        restaurant.setOpeningTimes(openingTimes);
        restaurant.setOfferTimes(offerTimes);

        return restaurant;
    }
}
