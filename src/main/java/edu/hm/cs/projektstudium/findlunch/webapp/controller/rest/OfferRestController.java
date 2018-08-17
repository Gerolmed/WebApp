package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.CourseType;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.TimeSchedule;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.CourseTypeRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferPhotoRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.service.RestaurantOfferService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.*;

/**
 * The class is responsible for handling rest calls related to offers.
 */
@RestController
@Validated
@Api(
		value="Angebote",
		description="Zum Zugriff auf Angebote.")
public class OfferRestController {

	/**
	 * The restaurant repository.
	 */
	private final RestaurantRepository restaurantRepo;
	
	private final OfferPhotoRepository offerPhotoRepo;

	private final CourseTypeRepository courseTypeRepo;

	/** The service for offer- and restaurant-related queries. */
	private final RestaurantOfferService restaurantOfferService;


	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferRestController.class);

	@Autowired
	public OfferRestController(OfferPhotoRepository offerPhotoRepo, CourseTypeRepository courseTypeRepo, RestaurantRepository restaurantRepo, RestaurantOfferService restaurantOfferService) {
		this.courseTypeRepo = courseTypeRepo;
		this.restaurantRepo = restaurantRepo;
		this.restaurantOfferService = restaurantOfferService;
		this.offerPhotoRepo = offerPhotoRepo;
	}

	/**
	 * Gets the offers of a given restaurant.
	 *
	 * @param request      the HttpServletRequest
	 * @param restaurantId the id of the restaurant
	 * @return the offers of the given restaurant with the corresponding coursetype
	 */
	@CrossOrigin
	@JsonView(OfferView.OfferRest.class)
	@ApiOperation(value = "Angebote eines Restaurants abrufen.", response = Map.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Angebote erfolgreich abgerufen"),
			@ApiResponse(code = 400, message = "Ungültige Restaurant-ID")
	})
	@RequestMapping(path = "/api/restaurants/{id}/offers", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Map<String, List<Offer>>> getOffersToRestaurant(@PathVariable("id") @ApiParam(value = "ID des Restaurants", required = true) int restaurantId, HttpServletRequest request) {

		List<Offer> result;
		Map<String, List<Offer>> offers = new LinkedHashMap<>();

		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		Restaurant restaurant = restaurantRepo.findById(restaurantId);
		if (restaurant != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			// check if restaurant has a TimeSchedule for today
			TimeSchedule ts = restaurant.getTimeSchedules()
					.stream()
					.filter(item -> item.getDayOfWeek().getDayNumber() == c.get(Calendar.DAY_OF_WEEK))
					.findFirst()
					.orElse(null);

			// only get offers, that are valid at the moment
			if (ts != null) {
				result = restaurantOfferService.getValidOffers(c, ts, restaurantId);
				if (result != null) {
					// Put the orders together with the coursetypes and order them by their sort by value
					for (CourseType course : courseTypeRepo.findByRestaurantIdOrderBySortByAsc(restaurantId)) {

						List<Offer> offersInCourse = new LinkedList<>();
						for (Offer offer : result) {
							if (offer.getCourseTypeId() == course.getId() && !offer.isSoldOut()) {
								offer.setDefaultPhoto(offerPhotoRepo.getOne(offer.getId()));
								offersInCourse.add(offer);
							}
						}
						if (!offersInCourse.isEmpty()) {
							offers.put(course.getName(), offersInCourse);
						}
						
					}
				}
			}
			return new ResponseEntity<>(offers, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 10 Angebote in der Nähe des Aufenthaltsorts abrufen.
	 *
	 * @param request   Der HttpServletRequest
	 * @param latitude  Breitengrad
	 * @param longitude Längengrad
	 * @param principal Principal des Nutzers
	 * @return 10 Angebote im Umkreis des aktuellen Aufenhaltsortes
	 */
	@CrossOrigin
	@JsonView(OfferView.OfferRest.class)
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(value = "Angebote rund um eine bestimmte Position abrufen", response = Map.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Angebote erfolgreich abgerufen"),
			@ApiResponse(code = 409, message = "Falsche Parameter angegeben")
	})
	@RequestMapping(path = "/api/offers", method = RequestMethod.GET, produces = "application/json")
	public List<Offer> getLocationBasedOffers(
			@RequestParam @NotNull @ApiParam(value = "Längengrad", required = true) float longitude,
			@RequestParam @NotNull @ApiParam(value = "Breitengrad", required = true) float latitude,
			HttpServletRequest request,
			Principal principal) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime((new Date()));

		// Username kann später noch für genauere Angebote genutzt werden wie seine Favoriten, Allergien, ...
		return restaurantOfferService.getOffersToLocation(longitude, latitude, 5, 2,
				true, calendar/*, ((User) ((Authentication) principal).getPrincipal()).getUsername()*/);
	}

	/**
	 * Exception handler for MethodArgumentTypeMismatchException.
	 *
	 * @param request the HttpServletRequest
	 * @param e       the exception
	 * @return the name of the exception class
	 */
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class, ValidationException.class})
	public ResponseEntity<String> exceptionHandler(Exception e, HttpServletRequest request) {
		LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		return new ResponseEntity<>(e.getClass().toString(), HttpStatus.CONFLICT);
	}
}