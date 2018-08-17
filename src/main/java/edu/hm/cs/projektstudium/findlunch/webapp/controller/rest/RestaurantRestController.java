package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.*;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantOfferService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;

/**
 * The class is responsible for handling rest calls related to get nearby restaurants.
 */
@RestController
@Validated
@Api(
		value="Restaurant",
		description="Restaurants abrufen.")
public class RestaurantRestController {

	/** The restaurant repository. */
	private final RestaurantRepository restaurantRepo;
	
	/** The points repository. */
	private final PointsRepository pointsRepository;

	/** The service for offer- and restaurant-related queries. */
	private final RestaurantOfferService restaurantOfferService;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RestaurantRestController.class);

	@Autowired
	public RestaurantRestController(RestaurantRepository restaurantRepo, PointsRepository pointsRepository, RestaurantOfferService restaurantOfferService) {
		this.restaurantRepo = restaurantRepo;
		this.pointsRepository = pointsRepository;
		this.restaurantOfferService = restaurantOfferService;
	}

	/**
	 * Gets the restaurants within a given radius with a flag showing if the
	 * restaurants is a favorite of the authenticated user. Ordered by service
	 * (ascending)
	 *
	 * @param request the HttpServletRequest
	 * @param longitude
	 *            the longitude used to get the center for the radius
	 *            calculation
	 * @param latitude
	 *            the latitude used to get the center for the radius calculation
	 * @param radius
	 *            the radius within the restaurants should be located
	 * @param principal
	 *            the principal to get the authenticated user
	 * @return the restaurants within the given radius with a flag showing if
	 *         the restaurants is a favorite of the authenticated user
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Restaurants in der Umgebung mit eigenen Favoriten abrufen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Restaurants erfolgreich abgerufen."),
			@ApiResponse(code = 401, message = "Nicht autorisiert.")
	})
	@RequestMapping(
			path = "/api/restaurants",
			headers = "Authentication",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<Restaurant> getRestaurantsAsAuthenticated(
			@RequestParam @NotNull @ApiParam( value = "Längengrad", required = true) float longitude,
			@RequestParam @NotNull @ApiParam( value = "Breitengrad", required = true) float latitude,
			@RequestParam @Min(0) @ApiParam(  value = "Bereich in Meter, um den herum gesucht werden soll.", required = true) int radius, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();

		List<Restaurant> restaurantList = restaurantOfferService.getAllRestaurants(longitude, latitude, radius);

		// Check favorites
		List<Restaurant> favorites = restaurantRepo.findByFavUsers_username(authenticatedUser.getUsername());

		for (Restaurant restaurant : restaurantList) {
			for (Restaurant favorite : favorites) {
				if (restaurant.getId() == favorite.getId()) {
					restaurant.setFavorite(true);
				}
			}
		}
		
		//add current points
		List<Points> pointsList = pointsRepository.findByCompositeKey_User_Id(authenticatedUser.getId());
		
		for (Restaurant restaurant : restaurantList) {
			for(Points points : pointsList){
				if(restaurant.getId() == points.getRestaurant().getId()){
					restaurant.setActualPoints(points.getPoints());
				}
			}
		}
		
		return restaurantList;
	}

	/**
	 * Gets the restaurants within a given radius. The flag "isFavorite" is
	 * always false. Ordered by service (ascending)
	 *
	 * @param request the HttpServletRequest
	 * @param longitude
	 *            the longitude used to get the center for the radius
	 *            calculation
	 * @param latitude
	 *            the latitude used to get the center for the radius calculation
	 * @param radius
	 *            the radius within the restaurants should be located
	 * @return the restaurants within the given radius. The flag "isFavorite" is
	 *         always false
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
			value = "Restaurants in der Umgebung abrufen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Restaurants erfolgreich abgerufen.")
	})
	@RequestMapping(
			path = "/api/restaurants",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<Restaurant> getRestaurants(
			@RequestParam @NotNull @ApiParam(value = "Längengrad", required = true) float longitude,
			@RequestParam @NotNull @ApiParam(value = "Breitengrad", required = true) float latitude,
			@RequestParam @Min(0) @ApiParam(
					value = "Bereich in Meter, um den herum gesucht werden soll.",
					required = true) int radius,
			HttpServletRequest request) {

		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		return restaurantOfferService.getAllRestaurants(longitude, latitude, radius);
	}

	/**
	 * Exception handler for MethodArgumentTypeMismatchException.
	 *
	 * @param request the HttpServletRequest
	 * @param e
	 *            the exception
	 * @return the name of the exception class
	 */
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class, ValidationException.class })
	public ResponseEntity<String> exceptionHandler(Exception e, HttpServletRequest request) {
		LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		return new ResponseEntity<>(e.getClass().toString(), HttpStatus.CONFLICT);
	}
}