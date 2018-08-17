/*
 * 
 */
package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The class is responsible for handling rest calls related to favorites of users.
 */
@RestController
@Api(value="Favoriten", description="Verwaltung der Favoriten des Users.")
public class FavoritesRestController {

	/** The restaurant repository. */
	private final RestaurantRepository restaurantRepository;

	/** The user repository. */
	private final UserRepository userRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(FavoritesRestController.class);

	@Autowired
	public FavoritesRestController(RestaurantRepository restaurantRepository, UserRepository userRepository) {
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}

	/**
	 * Register a restaurant as a favorite for a given user.
	 *
	 * @param request the HttpServletRequest
	 * @param restaurantId
	 *            the id of the restaurant
	 * @param principal
	 *            the principal to get the authenticated user
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Restaurant als Favoriten eines Benutzers registrieren.",
			response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registrierung erfolgreich."),
			@ApiResponse(code = 401, message = "Nicht autorisiert."),
			@ApiResponse(code = 409, message = "Restaurant nicht gefunden.")
	})
	@RequestMapping(
			path = "/api/register_favorite/{restaurantId}",
			method = RequestMethod.PUT,
			produces = "application/json")
	public ResponseEntity<Integer> registerFavorite(
			@PathVariable
			@ApiParam(
					value = "ID des Restaurants, das als Favorit zu registrieren ist.",
					required = true)
            Integer restaurantId,
			Principal principal, HttpServletRequest request) {

		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());

		Restaurant r = restaurantRepository.findById(restaurantId);
		if (r == null) {
			// Restaurant not found
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with the id " + restaurantId + " could not be found in the database."));
			return new ResponseEntity<>(3, HttpStatus.CONFLICT);
		}

		if (authenticatedUser.getFavorites().stream()
                .filter(item -> item.getId() == r.getId()).findFirst()
                .orElse(null) == null) {
            // add restaurant to users favorites
            authenticatedUser.getFavorites().add(r);
            userRepository.save(authenticatedUser);
		}
        // ELSE: Noting to do - restaurant already exists in favorites

		return new ResponseEntity<>(0, HttpStatus.OK);
	}

	/**
	 * Unregister a restaurant as a favorite for a given
	 *
	 * @param request the HttpServletRequest
	 * @param restaurantId
	 *            the id of the restaurant
	 * @param principal
	 *            the principal to get the authenticated user
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
	        value = "Restaurant als Favoriten eines Benutzer entfernen.",
            response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Deregistrierung erfolgreich."),
			@ApiResponse(code = 401, message = "Nicht autorisiert."),
			@ApiResponse(code = 409, message = "Restaurant nicht gefunden.")
	})
	@RequestMapping(
	        path = "/api/unregister_favorite/{restaurantId}",
            method = RequestMethod.DELETE,
            produces = "application/json")
	public ResponseEntity<Integer> unregisterFavorite(
	        @PathVariable("restaurantId")
            @ApiParam(
                    value = "ID des Restaurants, das als Favorit zu entfernen ist.",
                    required = true)
            Integer restaurantId,
			Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());

		Restaurant r = restaurantRepository.findById(restaurantId);
		if (r == null) {
			// Restaurant not found
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The restaurant with the id " + restaurantId + " could not be found in the database."));
			return new ResponseEntity<>(3, HttpStatus.CONFLICT);
		}

		Restaurant possibleMatch = authenticatedUser.getFavorites().stream().filter(item -> item.getId() == r.getId())
				.findFirst().orElse(null);
		if (possibleMatch != null) {
			// Remove restaurant from users' favorites
			authenticatedUser.getFavorites().remove(possibleMatch);
			userRepository.save(authenticatedUser);
		}
        // ELSE: Nothing to do - Restaurant does not exist in users' favorites

		return new ResponseEntity<>(0, HttpStatus.OK);
	}
}
