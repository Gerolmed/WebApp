package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantAddCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantDeleteCategory;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.restaurant.RestaurantValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.service.CountryService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.SalesPersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.security.Principal;
import java.util.Base64;
import java.util.UUID;

/**
 * The class is responsible for handling http calls related to the restaurant controller in the swa.
 */
@Controller
@Scope("session")
public class RestaurantControllerSWA {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SalesPersonService salesPersonService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private RestaurantValidator restaurantValidator;

    /**
     * Creates a new empty restaurant in the model.
     * @param model The model
     * @return The created restaurant
     */
    @RequestMapping(value = "/swa/newRestaurant", method = RequestMethod.GET)
    public String emptyRestaurant(Model model) {

        Restaurant restaurant = preparedRestaurantForNewRestaurant();
        model = getRestaurantModel(restaurant, model);
        model.addAttribute("offerLinkDisabled", true);

        return "swa_restaurant";
    }

    /**
     * Loads the requested restaurant into the model, if the user has access.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurantId Id of the restaurant
	 * @param request request the HttpServletRequest
	 * @return The restaurant
     */
    //Loads the requested restaurant into the model, if the user has access.
    @RequestMapping(value = "/swa/restaurant", method = RequestMethod.GET)
    public String getRestaurant(Model model, @RequestParam("id") int restaurantId, HttpServletRequest request) {

        //Checks if the restaurant exists - (security check, if the call parameter has been altered manually)
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if(restaurant == null) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        //Checks if the user is allowed to see the requested restaurant. (security check, if the call parameter has been altered manually)
        if(!restaurantService.restaurantAssignedToSalesPerson(restaurantId)) {
            return "redirect:/swa/home?noValidAccessToRestaurant";
        }

        restaurantService.addRestaurantToRestaurantTransactionStore(restaurant);
        request.getSession().setAttribute("restaurantId", restaurantId);
        Restaurant preparedRestaurant = preparedRestaurantForExistingRestaurant(restaurantId);
        model = getRestaurantModel(preparedRestaurant, model);

        return "swa_restaurant";
    }

    /**
     * Adds a new category to a restaurant.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurantAddCategory The category to add
	 * @param request request the HttpServletRequest
	 * @return The restaurant page
     */
    @RequestMapping(value = "/swa/restaurant/addCategory", method = RequestMethod.POST)
    public String processAddNewCategory(Model model, RestaurantAddCategory restaurantAddCategory, HttpServletRequest request) {

        int restaurantId;
        if(request.getSession().getAttribute("restaurantId") == null) {
            //The user used the forth and back buttons of the browser to navigate through to the page. Therefore the session attributes are not set correctly.
            return "redirect:/swa/home?doNotUseForthAndBackOfTheBrowserToNavigate";
        }
        restaurantId = (int) request.getSession().getAttribute("restaurantId");

        restaurantAddCategory.setRestaurantId(restaurantId);

        if(restaurantAddCategory.getName() == null) {
            return "redirect:/swa/restaurant?id=" + restaurantId;
        }

        restaurantService.addCategoryToRestaurant(restaurantAddCategory);
        return "redirect:/swa/restaurant?id=" + restaurantId;
    }

    /**
     * Deletes a existing category of a restaurant.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurantDeleteCategory Category to delete
	 * @param request request the HttpServletRequest
	 * @return The restaurant page
     */
    @RequestMapping(value = "/swa/restaurant/deleteCategory", method = RequestMethod.POST)
    public String processDeleteExistingCategory(Model model, RestaurantDeleteCategory restaurantDeleteCategory, HttpServletRequest request) {

        if(request.getSession().getAttribute("restaurantId") == null) {
            //The user used the forth and back buttons of the browser to navigate through to the page. Therefore no session attributes are set.
            return "redirect:/swa/home?doNotUseForthAndBackOfTheBrowserToNavigate";
        }
        int restaurantId = (int) request.getSession().getAttribute("restaurantId");

        restaurantDeleteCategory.setRestaurantId(restaurantId);

        if(restaurantDeleteCategory.getName() == null) {
            return "redirect:/swa/restaurant?id=" + restaurantId;
        }

        restaurantService.deleteCategoryFromRestaurant(restaurantDeleteCategory);
        return "redirect:/swa/restaurant?id=" + restaurantId;
    }

    /**
     * Saves changes made to the restaurant or creates a new restaurant.
     *
	 * @param model Model in which necessary object are placed to be displayed on the website
	 * @param restaurant The restaurant
	 * @param restaurantBinder Binder of the restaurant
	 * @param principal The principal
	 * @return The restaurant
     */
    @RequestMapping(value = "/swa/saveRestaurant", method = RequestMethod.POST)
    // TODO: Wieder validieren.
    public String processRestaurant(Model model, Principal principal, Restaurant restaurant, BindingResult restaurantBinder) {

        if(restaurantBinder.hasErrors()) {
            restaurant.setBase64Encoded(Base64.getEncoder().encodeToString(restaurant.getQrUuid()));
            Restaurant restaurantWithDayNumbers = restaurantService.setDayNumbers(restaurant);
            model = getRestaurantModel(restaurantWithDayNumbers, model);

            return "swa_restaurant";
        }

        //Security check for the bound restaurant fields
        String[] suppressedFields = restaurantBinder.getSuppressedFields();
        if (suppressedFields.length > 0) {
            throw new RuntimeException("Attempting to bind disallowed fields: " +
                    StringUtils.arrayToCommaDelimitedString(suppressedFields));
        }
        int restaurantId = restaurant.getId();

        if (restaurant.getIdOfSalesPerson() == 0) {
            SalesPerson sp = salesPersonService.getSalesPersonByEmail(principal.getName());
            restaurant.setIdOfSalesPerson(sp.getId());
        }

        //Checks if it is a new Restaurant or a change to an existing one
        if (restaurantId == 0) {
            restaurantService.saveRestaurant(restaurant);
            return "redirect:/swa/home?newRestaurantAddedSuccessfully";
        } else {
            if (restaurantService.restaurantHasBeenAlteredMeanwhile(restaurant)) {
                return "redirect:/swa/home?restaurantWasChangedMeanwhile";
            } else {
                restaurantService.saveRestaurant(restaurant);
                return "redirect:/swa/home?restaurantChangeSuccess";
            }
        }
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder restaurantBinder) {
        restaurantBinder.setAllowedFields(
                "id",
                "customerId",
                "name",
                "street",
                "streetNumber",
                "zip",
                "city",
                "country",
                "locationLatitudeAsString",
                "locationLongitudeAsString",
                "email",
                "phone",
                "url",
                "restaurantUuid",
                "qrUuid",
                "offerModifyPermission",
                "blocked",
                "openingTimes[0].startTime","openingTimes[0].endTime","openingTimes[1].startTime","openingTimes[1].endTime","openingTimes[2].startTime","openingTimes[2].endTime","openingTimes[3].startTime","openingTimes[3].endTime","openingTimes[4].startTime","openingTimes[4].endTime","openingTimes[5].startTime","openingTimes[5].endTime","openingTimes[6].startTime","openingTimes[6].endTime",
                "offerTimes[0].startTime","offerTimes[0].endTime","offerTimes[1].startTime","offerTimes[1].endTime","offerTimes[2].startTime","offerTimes[2].endTime","offerTimes[3].startTime","offerTimes[3].endTime","offerTimes[4].startTime","offerTimes[4].endTime","offerTimes[5].startTime","offerTimes[5].endTime","offerTimes[6].startTime","offerTimes[6].endTime",
                "restaurantTypeAsString",
                "kitchenTypesAsString",
                "idOfSalesPerson"
        );
        restaurantBinder.setValidator(restaurantValidator);
    }



    /**
     * Used to prepare an restaurant object of an existing restaurant for its injection into the model.
     * @param restaurantId Id of the restaurant
     * @return the restaurant object
     */
    private Restaurant preparedRestaurantForExistingRestaurant(int restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        restaurant.setBase64Encoded(Base64.getEncoder().encodeToString(restaurant.getQrUuid()));
        restaurant.restaurantTimeContainerFiller();
        restaurant.orderRestaurantTimeContainers(); //Ensure that the days of a week are shown in the correct order.
        restaurant.restaurantKitchenTypesAsStringFiller();
        restaurant.setLocationLatitudeAsString(String.valueOf(restaurant.getLocationLatitude()));
        restaurant.setLocationLongitudeAsString(String.valueOf(restaurant.getLocationLongitude()));

        return restaurant;
    }

    /**
     * Used to prepare an restaurant object of a new restaurant for its injection into the model.
     * @return the restaurant object
     */
    //Used to prepare an restaurant object of a new restaurant for its injection into the model
    private Restaurant preparedRestaurantForNewRestaurant() {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Restaurant restaurant = new Restaurant();
        restaurant.setCustomerId(restaurantService.getUniqueCustomerId());

        String restaurantUUID = UUID.randomUUID().toString();
        restaurant.setRestaurantUuid(restaurantUUID);
        restaurant.setQrUuid(restaurantService.createQRCode(restaurantUUID));
        restaurant.setBase64Encoded(Base64.getEncoder().encodeToString(restaurant.getQrUuid()));
        restaurant.setOpeningTimes(restaurantService.populateRestaurantTimeDayNumber());
        restaurant.setOfferTimes(restaurantService.populateRestaurantTimeDayNumber());
        restaurant.restaurantKitchenTypesAsStringFiller();
        restaurant.setSalesPerson(salesPersonService.getSalesPersonByEmail(loggedInUser));

        return restaurant;
    }

    /**
     * Gets the restaurant model for the currently logged in user.
     * @param restaurant the restaurant
     * @param model the model
     * @return the restaurant model
     */
    private Model getRestaurantModel(Restaurant restaurant, Model model) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("restaurantList", restaurantService.getAllRestaurantNamesForSalesPerson(loggedInUser));
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("restaurantTypes", restaurantService.getAllRestaurantTypes());
        model.addAttribute("kitchenTypes", restaurantService.getAllKitchenTypes());
        model.addAttribute("restaurantAddCategory", new RestaurantAddCategory());
        model.addAttribute("restaurantDeleteCategory", new RestaurantDeleteCategory());

        return model;
    }
}
