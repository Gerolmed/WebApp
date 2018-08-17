package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.*;

import java.util.List;

/**
 * Interface for services related to the database reader service.
 */
public interface DbReaderService {

    SalesPerson getSalesPersonByEmail(String email);

    SalesPerson getSalesPersonById(int id);

    List<Country> getAllCountries();

    List<Restaurant> getAllRestaurantsOfSalesPerson(String email);

    Restaurant getRestaurantById(int id);

    List<ToDo> getAllToDosOfSalesPerson(String email);

    List<RestaurantType> getAllRestaurantTypes();

    Restaurant getRestaurantByCustomerId(int id);

    List<KitchenType> getAllKitchenTypes();

    List<Offer> getAllOffersOfRestaurant(int restaurantId);

    List<Offer> getAllOffersOfRestaurant(int restaurantId, String courseType);

    List<Offer> getAllOffersOfRestaurantAndCourseTypeNull(int restaurantId);

    List<CourseType> getAllCourseTypesOfRestaurant(int restaurantId);

    Offer getOffer(int offerId);

    List<Additive> getAllAdditives();

    List<Allergenic> getAllAllergenic();

    OfferPhoto getOfferPhoto(int offerPhotoId);

    ToDo getToDoById(int toDoId);

    List<Reservation> getAllOfferReservationsByRestaurantId(int restaurantId);

    List<DonationPerMonth> getAllDonationsByRestaurantId(int restaurantId);

}
