package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantTypeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
/**
 * The class is responsible for handling rest calls related to RestaurantTypes.
 */
@RestController
@Api(
		value="Restauranttypen",
		description="Verwaltung der Restauranttypen.")
public class RestaurantTypeRestController {

	/** The restaurant type repository. */
	private final RestaurantTypeRepository typeRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RestaurantTypeRestController.class);

	@Autowired
	public RestaurantTypeRestController(RestaurantTypeRepository typeRepository) {
		this.typeRepository = typeRepository;
	}

	/**
	 * Gets all RestaurantTypes.
	 *
	 * @param request the HttpServletRequest
	 * @return all RestaurantTypes
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
			value = "Alle Restaurant-Typen abrufen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Restaurant-Typen erfolgreich abgerufen.")
	})
	@RequestMapping(
			path = "/api/restaurant_types",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<RestaurantType> getAllRestaurantTypes(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return typeRepository.findAll();
	}
}
