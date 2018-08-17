
package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.offer.OfferValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.service.HibernateService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.OfferService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.ArrayList;
import java.util.List;

/**
 * The class is responsible for handling http calls related to the offer controller in the swa.
 */
@Controller
@Scope("session")
public class OfferControllerSWA {

    @Autowired
    private OfferService offerService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private OfferValidator offerValidator;

    @Autowired
    private HibernateService hibernateService;

    /**
     * Create a new offer.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param request request the HttpServletRequest
	 * @return The offer
     */
    @RequestMapping(value = "/swa/emptyOffer")
    public String emptyOffer(Model model, HttpServletRequest request) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        request.getSession().setAttribute("newOffer", false);
        request.getSession().setAttribute("offerId", 0);
        request.getSession().setAttribute("restaurantId", 0);
        model.addAttribute("restaurantList", restaurantService.getAllRestaurantNamesForSalesPerson(loggedInUser));
        model.addAttribute("offer", new Offer());
        model.addAttribute("dataInputDisabled", true);
        model.addAttribute("restaurantName", "");
        model.addAttribute("allergenicsList", offerService.getAllAllergenic());
        model.addAttribute("additivesList", offerService.getAllAdditives());
        model = offerService.prepareOfferPictures(model, new Offer());

        return "swa_offer";
    }

    /**
     * Create new offer for a restaurant.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurantId Id of the restaurant
	 * @param request request the HttpServletRequest
	 * @return The offer
     */
    @RequestMapping(value = "/swa/newOfferForRestaurant", method = RequestMethod.GET)
    public String newOfferForRestaurant(Model model, @RequestParam("id") int restaurantId, HttpServletRequest request) {

        //Checks if the restaurant exists - (security check, if the call parameter has been altered manually)
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if(restaurant == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to access the restaurant. (security check, if the call parameter has been altered manually)
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        request.getSession().setAttribute("newOffer", true);
        request.getSession().setAttribute("offerId", 0);
        request.getSession().setAttribute("restaurantId", restaurantId);
        request.getSession().setAttribute("changeRequestId" , 0);

        Offer offer = new Offer();
        offer.setIdOfRestaurant(restaurantId);
        offer.offerTimesContainerFiller(restaurantService.getRestaurantById(restaurantId));

        model = prepareModelForGivenRestaurant(model, restaurantId);
        model = offerService.prepareOfferPictures(model, offer);
        model.addAttribute("offer", offer);

        return "swa_offer";
    }

     /**
     * Gets a existing offer.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param offerId Id of the offer
	 * @param request request the HttpServletRequest
	 * @return The offer
     */
    @RequestMapping(value = "/swa/offer", method = RequestMethod.GET)
    public String getExistingOffer(Model model, @RequestParam("id") int offerId, HttpServletRequest request) {

        //Checks if the offer exists - (security check, if the call parameter has been altered manually)
        Offer offer = offerService.getOffer(offerId);
        if(offer == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to access the offer. (security check, if the call parameter has been altered manually)
        Restaurant restaurant = offer.getRestaurant();
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        int restaurantId = restaurant.getId();
        int changeRequestId = offer.getChangeRequestId();

        request.getSession().setAttribute("newOffer", false);
        request.getSession().setAttribute("offerId", offerId);
        request.getSession().setAttribute("restaurantId", restaurantId);
        request.getSession().setAttribute("commentOfLastChange", offer.getCommentOfLastChange());
        request.getSession().setAttribute("changeRequestId" , changeRequestId);

        offerService.addOfferToTransactionStore(offer);
        Offer preparedExistingOffer = offerService.prepareExistingOffer(offer, restaurant);

        model = prepareModelForGivenRestaurant(model, restaurantId);
        model = offerService.prepareOfferPictures(model, preparedExistingOffer);
        model.addAttribute("offer", preparedExistingOffer);

        return "swa_offer";
    }

    /**
     * Deletes the image of a offer.
     *
	 * @param offerPhotoId Id of the offer image
	 * @param request request the HttpServletRequest
	 * @return The offer
     */
    @RequestMapping(value = "/swa/offer/remove")
    public String deleteOfferImage(@RequestParam("offerPhotoId") int offerPhotoId, HttpServletRequest request) {

        //Checks if the offerPhoto exists - (security check, if the call parameter has been altered manually)
        OfferPhoto offerPhoto = offerService.getOfferPhoto(offerPhotoId);
        if(offerPhoto == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to access the offerPhoto. (security check, if the call parameter has been altered manually)
        Offer offer = hibernateService.initializeAndUnproxy(offerPhoto.getOffer());
        Restaurant restaurant = hibernateService.initializeAndUnproxy(offer.getRestaurant());
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        if(request.getSession().getAttribute("offerId") == null) {
            //The user used the forth and back buttons of the browser to navigate through to the page. Therefore no session attributes are set.
            return "redirect:/swa/home?doNotUseForthAndBackOfTheBrowserToNavigate";
        }

        int offerId = (int) request.getSession().getAttribute("offerId");
        offerService.deleteOfferPhoto(offerPhotoId);

        return "redirect:/swa/offer?id=" + offerId;
    }

    /**
     * Saves an offer.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param offer The offer
	 * @param offerBinder Binder of the offer
	 * @param request request the HttpServletRequest
	 * @param homeFlag The home flag
	 * @param offerOverviewFlag The offer overview flag
	 * @return The offer
     */
    @RequestMapping(value = "/swa/saveOffer", method = RequestMethod.POST)
    public String saveOffer(Model model, Offer offer, BindingResult offerBinder, HttpServletRequest request,
                            @RequestParam(required = false, value = "home") String homeFlag,
                            @RequestParam(required = false, value = "offerOverview") String offerOverviewFlag) {

        if(request.getSession().getAttribute("restaurantId") == null) {
            //The user used the forth and back buttons of the browser to navigate through to the page. Therefore no session attributes are set.
            return "redirect:/swa/home?doNotUseForthAndBackOfTheBrowserToNavigate";
        }

        int restaurantId = (int) request.getSession().getAttribute("restaurantId");
        int offerId = (int) request.getSession().getAttribute("offerId");
        boolean newOffer = (boolean) request.getSession().getAttribute("newOffer");
        String commentOfLastChange = (String) request.getSession().getAttribute("commentOfLastChange");
        int changeRequestId = (int) request.getSession().getAttribute("changeRequestId");

        //Security check for the bound offer fields
        String[] suppressedFields = offerBinder.getSuppressedFields();
        if (suppressedFields.length > 0) {
            throw new RuntimeException("Attempting to bind disallowed fields: " + StringUtils.arrayToCommaDelimitedString(suppressedFields));
        }

        if (offerBinder.hasErrors()) {
            offer.setIdOfRestaurant(restaurantId);
            offer.offerTimesContainerFiller(restaurantService.getRestaurantById(restaurantId));
            offer.setId(offerId);

            model = prepareModelForGivenRestaurant(model, restaurantId);
            model = offerService.prepareOfferPictures(model, offer);
            model.addAttribute("offer", offer);

            return "swa_offer";
        }

        //Keeps the former change comment if no new change comment has been entered.
        if (offer.getNewChangeComment().equals("")) {
            offer.setCommentOfLastChange(commentOfLastChange);
        }

        /*
        * 1) Distinguish between a new offer and an offer update.
        * 2) Checks if the offer has been altered while the user worked on it.
         */
        if (newOffer == true && offerId == 0) {
            offer.setIdOfRestaurant(restaurantId);
            offerService.saveOffer(offer);

            if(offerOverviewFlag != null) {
                return "redirect:/swa/offerOverviewByRestaurant?id=" + restaurantId;
            } else {
                //homeFlag is set now
                return "redirect:/swa/home?newOfferAddedSuccessfully";
            }
        } else {
            if (offerService.offerHasBeenAlteredMeanwhile(offerId)) {
                return "redirect:/swa/home?offerWasChangedMeanwhile";
            } else if(newOffer == false && offerId != 0) {
                offer.setIdOfRestaurant(restaurantId);
                offer.setId(offerId);
                offer.setChangeRequestId(changeRequestId);
                offerService.saveOffer(offer);

                if(offerOverviewFlag != null) {
                    return "redirect:/swa/offerOverviewByRestaurant?id=" + restaurantId;
                }

                if(homeFlag != null) {
                    return "redirect:/swa/home?offerChangeSuccess";
                }
            }
            return "redirect:/swa/home?offerControllerError";
        }
    }

    /**
     * Prepares model for a given restaurant.
     * @param model The model
     * @param restaurantId Id of the restaurant.
     * @return The prepared model for the restaurant
     */
    private Model prepareModelForGivenRestaurant(Model model, int restaurantId) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("restaurantList", restaurantService.getAllRestaurantNamesForSalesPerson(loggedInUser));
        model.addAttribute("offerList", offerService.getAllOffersOfRestaurant(restaurantId));
        model.addAttribute("dataInputDisabled", false);
        model.addAttribute("restaurantName", restaurantService.getRestaurantById(restaurantId).getName());
        model.addAttribute("allergenicsList", offerService.getAllAllergenic());
        model.addAttribute("additivesList", offerService.getAllAdditives());

        List<CourseType> courseTypes = null;
        try {
            courseTypes = restaurantService.getAllCourseTypesOfRestaurant(restaurantId);
        } catch (Exception e) {
            // Restaurant has no course types so far.
        }

        if(courseTypes == null) {
            courseTypes = new ArrayList<>();
        }

        model.addAttribute("courseTypes", courseTypes);

        return model;
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder offerBinder) {
        offerBinder.setAllowedFields(
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
                "home",
                "offerOverview"
        );
        offerBinder.setValidator(offerValidator);
    }

}
