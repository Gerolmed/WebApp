package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer.OfferValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.service.HibernateService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.OfferChangeRequestService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.OfferService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * The class is responsible for handling http calls related to changing offers in the swa.
 */
@Controller
@Scope
public class OfferChangeRequestControllerSWA {

    @Autowired
    private OfferChangeRequestService offerChangeRequestService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OfferValidator offerValidator;

    @Autowired
    private HibernateService hibernateService;

    /**
     * Get a change request for offers.
     * @param model Model in which necessary object are placed to be displayed on the website.
     * @param toDoId Id of the toDo object
     * @param request the HttpServletRequest
     * @return the offer change request
     */
    @RequestMapping(value = "/swa/offerChangeRequest", method = RequestMethod.GET)
    public String getOfferChangeRequest(Model model, @RequestParam("id") int toDoId, HttpServletRequest request) {

        /*
        The ToDo-Object contains the offerId for the offer which has a change Request. Therefore
        only one changeRequest per offer is possible. The offer-object contains the
        id (swa_change_request_id) for the offer-object which holds the information for
        the change request.
         */

        //Checks if the offer exists - (security check, if the call parameter has been altered manually)
        ToDo toDo = offerChangeRequestService.getToDoById(toDoId);
        if(toDo == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }
        Restaurant restaurant = hibernateService.initializeAndUnproxy(toDo.getRestaurant());

        //Checks if the user is allowed to see the requested offer. (security check, if the call parameter has been altered manually)
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        Offer existingOffer = hibernateService.initializeAndUnproxy(toDo.getOffer());
        Offer changedOffer = offerService.getOffer(existingOffer.getChangeRequestId());

        request.getSession().setAttribute("commentOfLastChange", existingOffer.getCommentOfLastChange());
        request.getSession().setAttribute("existingOfferId", existingOffer.getId()); //Id of the offer to which to change request belongs
        request.getSession().setAttribute("changedOfferId", changedOffer.getId()); //Id of the change request
        request.getSession().setAttribute("restaurantId", restaurant.getId());
        request.getSession().setAttribute("toDoId", toDoId);

        offerService.addOfferToTransactionStore(hibernateService.initializeAndUnproxy(existingOffer));
        Offer preparedChangedOffer = offerService.prepareExistingOffer(changedOffer, restaurant);
        Offer preparedExistingOffer = offerService.prepareExistingOffer(existingOffer, restaurant);
        preparedChangedOffer = offerChangeRequestService.prepareKeepImagesTags(preparedExistingOffer, preparedChangedOffer);

        model.addAttribute("offer", preparedChangedOffer);
        model.addAttribute("existingOffer", preparedExistingOffer);
        model = offerService.prepareOfferPictures(model, changedOffer); //prepares the images for the changedOffer
        model = offerChangeRequestService.prepareIdsOfOfferChangeRequestImages(model, preparedChangedOffer, null);
        model = offerChangeRequestService.prepareOfferPicturesForExistingOffer(model, existingOffer); //prepares the images for the existingOffer
        model.addAttribute("restaurantName", restaurantService.getRestaurantById(restaurant.getId()).getName());
        model.addAttribute("allergenicsList", offerService.getAllAllergenic());
        model.addAttribute("additivesList", offerService.getAllAdditives());
        model.addAttribute("courseTypesList", offerChangeRequestService.getCourseTypes(restaurant.getId()));
        model.addAttribute("toDoId", toDoId);
        model = offerChangeRequestService.addAttribtueChangesToModel(model, false, preparedExistingOffer, preparedChangedOffer);

        return "swa_offerChangeRequest";
    }

    /**
     * Save a change request for offers.
     * @param model Model in which necessary object are placed to be displayed on the website.
     * @param changedOffer the changed offer
     * @param changedOfferBinder Binder of the changed offer
     * @param request the HttpServletRequest
     * @return the offer change request
     */
    @RequestMapping(value = "/swa/saveOfferChangeRequest", method = RequestMethod.POST)
    public String saveOfferChangeRequest(Model model, Offer changedOffer, BindingResult changedOfferBinder, HttpServletRequest request) {

        int toDoId;
        if(request.getSession().getAttribute("toDoId") == null) {
            //The user used the forth and back buttons of the browser to navigate through to the page. Therefore no session attributes are set.
            return "redirect:/swa/home?doNotUseForthAndBackOfTheBrowserToNavigate";
        }

        toDoId = (int) request.getSession().getAttribute("toDoId");
        int existingOfferId = (int) request.getSession().getAttribute("existingOfferId");
        int changedOfferId = (int) request.getSession().getAttribute("changedOfferId");
        int restaurantId = (int) request.getSession().getAttribute("restaurantId");
        String commentOfLastChange = (String) request.getSession().getAttribute("commentOfLastChange");
        Offer existingOffer = offerService.getOffer(existingOfferId);

        //Security check for the bound offer fields
        String[] suppressedFields = changedOfferBinder.getSuppressedFields();
        if (suppressedFields.length > 0) {
            throw new RuntimeException("Attempting to bind disallowed fields: " + StringUtils.arrayToCommaDelimitedString(suppressedFields));
        }

        //Validator
        if (changedOfferBinder.hasErrors()) {

            ToDo toDo = offerChangeRequestService.getToDoById(toDoId);
            Restaurant restaurant = toDo.getRestaurant();
            Offer originalChangedOffer = offerService.getOffer(changedOfferId);

            model.addAttribute("offer", changedOffer);
            model.addAttribute("existingOffer", offerService.prepareExistingOffer(existingOffer, restaurant));
            model = offerService.prepareOfferPictures(model, changedOffer);
            model = offerChangeRequestService.prepareIdsOfOfferChangeRequestImages(model, changedOffer, originalChangedOffer);
            model = offerChangeRequestService.prepareOfferPicturesForExistingOffer(model, existingOffer);
            model.addAttribute("restaurantName", restaurantService.getRestaurantById(restaurantId).getName());
            model.addAttribute("allergenicsList", offerService.getAllAllergenic());
            model.addAttribute("additivesList", offerService.getAllAdditives());
            model.addAttribute("courseTypesList", offerChangeRequestService.getCourseTypes(restaurantId));
            model.addAttribute("toDoId", toDoId);
            model = offerChangeRequestService.addAttribtueChangesToModel(model, true, null, null);

            return "swa_offerChangeRequest";
        }

        //Keeps the former change comment if no new change comment has been entered.
        if (changedOffer.getNewChangeComment().equals("")) {
            changedOffer.setCommentOfLastChange(commentOfLastChange);
        }

        //Checks if the offer of the offerChangeRequest has been altered while the user worked on it.
        if(offerService.offerHasBeenAlteredMeanwhile(existingOfferId)) {
            return "redirect:/swa/home?offerWasChangedMeanwhile";
        } else {
            changedOffer.setId(existingOfferId);
            changedOffer.setIdOfRestaurant(restaurantId);
            offerChangeRequestService.saveOfferChangeRequest(changedOfferId, changedOffer, existingOffer, toDoId);
            return "redirect:/swa/home?offerChangeRequestSuccess";
        }
    }

    /**
     * Deletes the offer change request.
     * @param toDoId Id of the toDo object
     * @return redirects the user to the page redirect:/swa/home?changeRequestDeleted
     */
    @RequestMapping(value = "/swa/offerChangeRequest/remove")
    public String deleteOfferChangeRequest(@RequestParam("toDoId") int toDoId) {

        //Checks if the todo exists - (security check, if the call parameter has been altered manually)
        ToDo toDo = offerChangeRequestService.getToDoById(toDoId);
        if(toDo == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }
        Restaurant restaurant = hibernateService.initializeAndUnproxy(toDo.getRestaurant());

        //Checks if the user is allowed to delete the offerChangeRequest. (security check, if the call parameter has been altered manually)
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        int offerToUpdateId = toDo.getOffer().getId();
        int offerToDeleteId = toDo.getOffer().getChangeRequestId();

        offerChangeRequestService.deleteOfferChangeRequest(offerToDeleteId, offerToUpdateId, toDoId);

        return "redirect:/swa/home?changeRequestDeleted";
    }

    /**
     * Deletes a offer photo.
     * @param offerPhotoId Id of the offer photo
     * @return redirects the user
     */
    @RequestMapping(value = "/swa/offerChangeRequest/removePhoto")
    public String deleteOfferChangeRequestPhoto(@RequestParam("Id") int offerPhotoId) {

        //Checks if the offerPhoto exists - (security check, if the call parameter has been altered manually)
        OfferPhoto offerPhoto = offerService.getOfferPhoto(offerPhotoId);
        if(offerPhoto == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to delete the offerPhoto. (security check, if the call parameter has been altered manually)
        Offer offer = hibernateService.initializeAndUnproxy(offerPhoto.getOffer());
        Restaurant restaurant = hibernateService.initializeAndUnproxy(offer.getRestaurant());
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        SalesPerson salesPerson = hibernateService.initializeAndUnproxy(restaurant.getSalesPerson());
        List<ToDo> allToDosOfSalesPerson = offerChangeRequestService.getAllToDosOfSalesPerson(salesPerson.getEmail());
        int offerId = offer.getId();
        int toDoId = 0;

        for(ToDo toDo : allToDosOfSalesPerson) {
            if(toDo.getOffer() != null) {
                Offer offerOfToDo = hibernateService.initializeAndUnproxy(toDo.getOffer());
                int changeRequestId = offerOfToDo.getChangeRequestId();

                if(offerId == changeRequestId) {
                    toDoId = toDo.getId();
                    break;
                }
            }
        }
        offerService.deleteOfferPhoto(offerPhotoId);

        return "redirect:/swa/offerChangeRequest?id=" + toDoId;
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder changedOfferBinder) {
        changedOfferBinder.setAllowedFields(
                "title",
                "priceAsString",
                "neededPointsAsString",
                "preparationTimeAsString",
                "description",
                "startDateAsString",
                "endDateAsString",
                "courseTypeAsString",
                "additivesAsString",
                "allergenicsAsString",
                "validnessDaysOfWeekAsString",
                "firstOfferImage",
                "secondOfferImage",
                "thirdOfferImage",
                "newChangeComment",
                "keepFirstImage",
                "keepSecondImage",
                "keepThirdImage",
                "offerPhotos"
        );
        changedOfferBinder.setValidator(offerValidator);
    }

}
