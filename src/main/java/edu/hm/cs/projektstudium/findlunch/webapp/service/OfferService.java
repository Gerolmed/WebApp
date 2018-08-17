package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Interface for services related to the offer service.
 */
public interface OfferService {

    List<Offer> getAllOffersOfRestaurant(int restaurantId);

    List<Offer> getAllOffersOfRestaurant(int restaurantId, String courseTypeAsString);

    List<Offer> getAllOffersOfRestaurantAndCourseTypeNull(int restaurantId);

    void deleteOffer(int offerId);

    Offer getOffer(int offerId);

    List<Additive> getAllAdditives();

    List<Allergenic> getAllAllergenic();

    void addOfferToTransactionStore(Offer offer);

    boolean offerHasBeenAlteredMeanwhile(int offerId);

    void saveOffer(Offer offer);

    OfferPhoto getOfferPhoto(int offerPhotoId);

    void deleteOfferPhoto(int offerPhotoId);

    Offer prepareExistingOffer(Offer offer, Restaurant restaurant);

    Model prepareOfferPictures(Model model, Offer offer);

    String getDefaultOfferImageBase64();

    boolean toDoEntryWithOfferExists(int offerId);
}
