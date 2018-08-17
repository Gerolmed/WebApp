package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantAddCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantDeleteCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantListForm;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;

import java.util.List;

/**
 * Interface for services related to restaurants.
 */
public interface RestaurantService {

    List<RestaurantListForm> getAllRestaurantNamesForSalesPerson(String email);

    Restaurant getRestaurantById(int id);

    boolean restaurantAssignedToSalesPerson(int id);

    int getUniqueCustomerId();

    void addCategoryToRestaurant(RestaurantAddCategory restaurantAddCategory);

    void deleteCategoryFromRestaurant(RestaurantDeleteCategory restaurantDeleteCategory);

    void addRestaurantToRestaurantTransactionStore(Restaurant restaurant);

    boolean restaurantHasBeenAlteredMeanwhile(Restaurant restaurant);

    void saveRestaurant(Restaurant restaurant);

    byte[] createQRCode(String restaurantUUID);

    List<RestaurantType> getAllRestaurantTypes();

    List<KitchenType> getAllKitchenTypes();

    List<RestaurantTimeContainer> populateRestaurantTimeDayNumber();

    List<CourseType> getAllCourseTypesOfRestaurant(int restaurantId);

    Restaurant setDayNumbers(Restaurant restaurant);
}
