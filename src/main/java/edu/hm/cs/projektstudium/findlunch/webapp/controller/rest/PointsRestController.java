package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;

/**
 * The Class is responsible for handling rest calls related to points.
 */
@RestController
@Api(
		value="Punkte",
		description="Verwaltung der Punkte je Nutzer.")
public class PointsRestController {

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(PointsRestController.class);
	
	/** The Points repository. */
	private final PointsRepository pointsRepository;

	@Autowired
	public PointsRestController(PointsRepository pointsRepository) {
		this.pointsRepository = pointsRepository;
	}

	/**
	 * Gets the points of an User.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return a List from the current points of the user
	 */
	@CrossOrigin
    @JsonView(PointsView.PointsRest.class)
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Punktestand des Benutzers abfragen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Punktestand zurückgegeben."),
			@ApiResponse(code = 401, message = "Nicht autorisiert.")
	})
	@RequestMapping(
			path = "/api/get_points",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<Points> getPointsOfAUser(/*@RequestParam(name ="user_id", required=true) int userId ,*/ Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		/*
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		if(authenticatedUser.getId() == userId){
			List<Points> pointsOfUser = pointsRepository.findByCompositeKey_User_Id(userId);
			return pointsOfUser;
		}
		return null;//kein zugriffsrecht, da anderer Benutzer*/
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		return pointsRepository.findByCompositeKey_User_Id(authenticatedUser.getId());
	}
	
	/**
	 * Gets the points of an User for a given Restaurant Id.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @param restaurantId Id of the restaurant
	 * @return a List from the current points of the user
	 */
	@CrossOrigin
	@JsonView(PointsView.PointsRest.class)
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Abrufen des Punktestands eines Benutzers zu einem bestimmten Restaurant.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Punktestand erfolgreich zurückgegeben."),
			@ApiResponse(code = 401, message = "Nicht autorisiert.")
	})
	@RequestMapping(
			path = "/api/get_points_restaurant/{restaurantId}",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<Points> getPointsOfAUserForRestaurant(
			@PathVariable("restaurantId")
            @ApiParam(
            		value = "ID des Restaurants, für das die Punkte eines Benutzers ausgegeben werden sollen.",
					required = true)
            int restaurantId, Principal principal, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		return pointsRepository.findByCompositeKey_User_IdAndCompositeKey_Restaurant_Id(authenticatedUser.getId(), restaurantId);
	}
}
