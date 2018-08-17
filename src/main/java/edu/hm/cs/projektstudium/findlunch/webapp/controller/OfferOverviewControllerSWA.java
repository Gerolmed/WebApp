package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.components.DateReOrder;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.service.HibernateService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.OfferService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is responsible for handling http calls related to the offer overview in the swa.
 */
@Controller
@Scope("session")
public class OfferOverviewControllerSWA {

    @Autowired
    private OfferService offerService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private HibernateService hibernateService;

    private DateReOrder dateReOrder = new DateReOrder();

    /**
     * Overview of an empty offer.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param request request the HttpServletRequest
	 * @return The offer overview
     */
    @RequestMapping(value = "/swa/emptyOfferOverview")
    public String emptyOfferOverview(Model model, HttpServletRequest request) {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        request.getSession().setAttribute("idForCancelButton", 0);
        model.addAttribute("restaurantList", restaurantService.getAllRestaurantNamesForSalesPerson(loggedInUser));
        model.addAttribute("disableCategoryFilter", true);

        return "swa_offerOverview";
    }

    /**
     * Gets offers of a given restaurant.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurantId Id of the restaurant
	 * @param request request the HttpServletRequest
	 * @return Offers of the given restaurant
     */
    //Loads the requested offers into the model, if the user has access.
    @RequestMapping(value = "/swa/offerOverviewByRestaurant")
    public String getOffersOfRestaurant(Model model, @RequestParam("id") int restaurantId, HttpServletRequest request) {

        //Checks if the user is allowed to see the requested restaurant. (security check, if the call parameter has been altered manually)
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurantId)) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        request.getSession().setAttribute("idForCancelButton", 0);
        request.getSession().setAttribute("restaurantId", restaurantId);
        request.getSession().setAttribute("pageStatus", "filteredByRestaurantId");
        model = prepareModel(model, restaurantId, loggedInUser);
        model.addAttribute("offerList", getOfferList(restaurantId));

        return "swa_offerOverview";
    }

    /**
     * Gets offers for a given course type.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param courseTypeAsString The course type in form of a string
	 * @param request request the HttpServletRequest
	 * @return Offers of the given restaurant
     */
    //Loads the requested offers into the model, filtered by course type
    @RequestMapping(value = "/swa/offerOverviewByCourseType")
    public String getOffersForCourseType(Model model, @RequestParam("courseType") String courseTypeAsString, HttpServletRequest request) {
        int restaurantId = (int) request.getSession().getAttribute("restaurantId");
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        request.getSession().setAttribute("pageStatus", "filteredByRestaurantIdAndCourseType");
        request.getSession().setAttribute("courseType", courseTypeAsString);
        request.getSession().setAttribute("idForCancelButton", restaurantId);
        model = prepareModel(model, restaurantId, loggedInUser);
        model.addAttribute("offerList", getOfferList(restaurantId, courseTypeAsString));

        return "swa_offerOverview";
    }

    /**
     * Cancels the offer overview.
     *
	 * @param request request the HttpServletRequest
	 * @return To the swa home screen or offer overview of restaurant if id was given.
     */
    //Custom cancel button
    @RequestMapping(value = "/swa/cancelOfferOverview")
    public String cancelOfferOverview(HttpServletRequest request) {
        int id = (int) request.getSession().getAttribute("idForCancelButton");

        if(id == 0) {
            return "redirect:/swa/home";
        } else {
            return "redirect:/swa/offerOverviewByRestaurant?id=" + id;
        }
    }

    /**
     * Delete an offer.
     *
	 * @param offerId Id of the offer
	 * @param request request the HttpServletRequest
	 * @return To an earlier webpage
     */
    //Delete an offer
    @RequestMapping(value = "/swa/offerOverview/remove")
    public String deleteOffer(@RequestParam("offerId") int offerId, HttpServletRequest request) {

        //Checks if the offer exists - (security check, if the call parameter has been altered manually)
        Offer offer = offerService.getOffer(offerId);
        if(offer == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to delete the offer. (security check, if the call parameter has been altered manually)
        Restaurant restaurant = hibernateService.initializeAndUnproxy(offer.getRestaurant());
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurant.getId())) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if an toDo_List entry exists
        if(!offerService.toDoEntryWithOfferExists(offerId)) {
            offerService.deleteOffer(offerId);
        } else {
            return "redirect:/swa/home?toDoListEntryForOfferExists";
        }

        String pageStatus = (String) request.getSession().getAttribute("pageStatus");
        if(pageStatus.equals("filteredByRestaurantId")) {
            return "redirect:/swa/offerOverviewByRestaurant?id=" + restaurant.getId();
        } else {
            String courseTypeAsString = (String) request.getSession().getAttribute("courseType");
            return "redirect:/swa/offerOverviewByCourseType?courseType=" + courseTypeAsString;
        }
    }

    /**
     * Prepares the model.
     * @param model The model
     * @param restaurantId Id of the restaurant
     * @param loggedInUser The logged in user
     * @return The model
     */
    private Model prepareModel(Model model, int restaurantId, String loggedInUser) {

        List<CourseType> courseTypeList = null;
        try {
            courseTypeList = restaurantService.getAllCourseTypesOfRestaurant(restaurantId);
        } catch (Exception e) {
            // Restaurant has no course types so far.
        }

        if(courseTypeList == null) {
            courseTypeList = new ArrayList<CourseType>();
        }
        courseTypeList.add(new CourseType("..."));

        model.addAttribute("restaurantName", restaurantService.getRestaurantById(restaurantId).getName());
        model.addAttribute("restaurantList", restaurantService.getAllRestaurantNamesForSalesPerson(loggedInUser));
        model.addAttribute("courseTypeList", courseTypeList);

        return model;
    }

    /**
     * Gets a list of offers by restaurantId and courseType.
     * @param restaurantId Id of the restaurant
     * @param courseTypeAsString The course type in string form
     * @return List of offers
     */
    private List<Offer> getOfferList(int restaurantId, String courseTypeAsString) {
        List<Offer> offerList = null;

        if(courseTypeAsString.equals("...")) {
            try {
                offerList = offerService.getAllOffersOfRestaurantAndCourseTypeNull(restaurantId);
            } catch (Exception e) {
                // The restaurant has no offers where the course type is null
            }
        } else {
            try {
                offerList = offerService.getAllOffersOfRestaurant(restaurantId, courseTypeAsString);
            } catch (Exception e) {
                // The restaurant has no offers so far
            }
        }

        if(offerList != null) {
            offerList = setStringsForDisplaying(offerList);
        }

        return offerList;
    }

    /**
     * Gets a list of offers by restaurantId.
     * @param restaurantId Id of the restaurant
     * @return List of offers
     */
    private List<Offer> getOfferList(int restaurantId) {
        List<Offer> offerList = null;

        try {
            offerList = offerService.getAllOffersOfRestaurant(restaurantId);
        } catch (Exception e) {
            // The restaurant has no offers so far
        }

        if(offerList != null) {
            offerList = setStringsForDisplaying(offerList);
        }

        return offerList;
    }

    /**
     * Displays the offers.
     * @param offerList List of offers
     * @return The offers
     */
    private List<Offer> setStringsForDisplaying(List<Offer> offerList) {

        if(offerList != null) {
            for (Offer offer : offerList) {
                try {
                    offer.setCourseTypeAsString(offer.getCourseType().getName());
                } catch (Exception e) {
                    // The offer has no assigned course type
                }

                try {
                    String date = offer.getStartDate().toString();
                    date = date.substring(0, date.length() - 11);
                    offer.setStartDateAsString(dateReOrder.reOrderDateString(date));
                } catch (Exception e) {
                    // The offer has no assigned start date
                }

                try {
                    String date = offer.getEndDate().toString();
                    date = date.substring(0, date.length() - 11);
                    offer.setEndDateAsString(dateReOrder.reOrderDateString(date));
                } catch (Exception e) {
                    // The offer has no assigned end date
                }
            }
        }
        return offerList;
    }
}