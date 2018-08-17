package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ToDo;
import org.springframework.ui.Model;

import java.util.List;

/**
 * Interface for services related to the offer change request service.
 */
public interface OfferChangeRequestService {

    ToDo getToDoById(int id);

    List<ToDo> getAllToDosOfSalesPerson(String email);

    List<CourseType> getCourseTypes(int restaurantId);

    void deleteOfferChangeRequest(int offerToDeleteId, int offerToUpdateId, int toDoId);

    void saveOfferChangeRequest(int offerChangeRequestId, Offer changedOffer, Offer existingOffer, int toDoId);

    Model addAttribtueChangesToModel (Model model, boolean allFalse, Offer preparedExistingOffer, Offer preparedChangedOffer);

    Model prepareOfferPicturesForExistingOffer(Model model, Offer offer);

    Model prepareIdsOfOfferChangeRequestImages(Model model, Offer preparedChangedOffer, Offer originalChangedOffer);

    Offer prepareKeepImagesTags(Offer preparedExistingOffer, Offer preparedChangedOffer);

}
