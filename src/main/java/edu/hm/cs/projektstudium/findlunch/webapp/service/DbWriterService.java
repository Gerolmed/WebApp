package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;

import java.util.List;

/**
 * Interface for services related to the database writer service.
 */
public interface DbWriterService {

    void setNewPassword(String userEmail, String encodedPassword);

    void setNewPassword(Integer userId, String encodedPassword);

    void saveProfileChange(ProfileForm profileForm);

    void addCategoryToRestaurant(List CourseTypes, int restaurantId);

    void deleteCategoryFromRestaurant(CourseType courseType);

    void saveRestaurant(Restaurant restaurant);

    void deleteOffer(int offerId);

    void saveOffer(Offer offer, boolean isOfferChangeRequest);

    void deleteOfferPhoto(int offerPhotoId);

    void deleteToDo(int toDoId);

    void deleteOfferChangeRequest(int offerToDeleteId, int offerToUpdateId, int ToDoId);

    void saveOfferChangeRequest(int offerChangeRequestId, Offer changedOffer, Offer existingOffer, int toDoId);

}
