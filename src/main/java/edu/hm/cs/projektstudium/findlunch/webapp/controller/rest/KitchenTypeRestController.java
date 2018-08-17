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
import edu.hm.cs.projektstudium.findlunch.webapp.model.KitchenType;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.KitchenTypeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The class is responsible for handling rest calls related to KitchenTypes.
 */
@RestController
@Api(
		value="Küchentyp",
		description="Anfragen zum Küchentyp.")
public class KitchenTypeRestController {

	/** The type repository. */
	private final KitchenTypeRepository typeRepository;
	
	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(KitchenTypeRestController.class);

	@Autowired
	public KitchenTypeRestController(KitchenTypeRepository typeRepository) {
		this.typeRepository = typeRepository;
	}

	/**
	 * Gets all KitchenTypes.
	 *
	 * @param request the HttpServletRequest
	 * @return all KitchenTypes
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
			value = "Abruf aller Küchentypen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Küchentypen erfolgreich zurückgegeben.")
	})
	@RequestMapping(
			path = "/api/kitchen_types",
			method = RequestMethod.GET,
			produces = "application/json")
	public List<KitchenType> getAllKitchenTypes(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return typeRepository.findAll();
	}
}
