package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Additive;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Allergenic;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AdditiveRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.AllergenicRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * The class is responsible for handling rest calls related to Ingredients.
 * Rest controllers mapping api.
 */
@RestController
@Api(
		value="Zutaten",
		description="Verwaltung der Zutaten.")
public class IngredientRestController {

	/** The Allergenic repository. */
	private final AllergenicRepository allergenicRepository;

	/** The Additives repository. */
	private final AdditiveRepository additiveRepository;

	/** The Additives repository. */
	private final OfferRepository offerRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(IngredientRestController.class);

	@Autowired
	public IngredientRestController(AllergenicRepository allergenicRepository,
									AdditiveRepository additiveRepository,
									OfferRepository offerRepository) {
		this.allergenicRepository = allergenicRepository;
		this.additiveRepository = additiveRepository;
		this.offerRepository = offerRepository;
	}

	/**
	 * Gets all allergenic.
	 *
	 * @param request the HttpServletRequest
	 * @return all allergenic
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
	        value = "Abruf aller Allergene.",
            response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Allergene erfolgreich zurückgegeben.")
	})
	@RequestMapping(
	        path = "/api/all_allergenic",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<Allergenic> getAllAllergenic(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return allergenicRepository.findAll();
	}

	/**
	 * Gets all allergenic for offer.
	 *
	 * @param request the HttpServletRequest
	 * @param offerId Id of the offer.
	 * @return all allergenic
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
	        value = "Abruf aller Allergene für ein Angebot.",
            response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Allergene erfolgreich zurückgegeben.")
	})
	@RequestMapping(
	        path = "/api/allergenicForOfferId/{offerId}",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<Allergenic> getAllergenicForOffer(
	        @PathVariable("offerId")
            @ApiParam(
                    value = "ID des Angebots, für das die Allergene bestimmt werden sollen.",
                    required = true)
            Integer offerId,
            HttpServletRequest request) {

		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		Offer offer = offerRepository.getOne(offerId);
		
		return offer.getAllergenic();
	}

	/**
	 * Gets all additives.
	 *
	 * @param request the HttpServletRequest
	 * @return all additives
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
	        value = "Abruf aller Zusaetze.",
            response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Zusätze erfolgreich zurückgegeben.")
	})
	@RequestMapping(
	        path = "/api/all_additives",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<Additive> getAllAdditives(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return additiveRepository.findAll();
	}
	
	/**
	 * Gets all additives for offer.
	 *
	 * @param request the HttpServletRequest
	 * @param offerId Id of the offer.
	 * @return all additives
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
	        value = "Abruf aller Zusätze zu einem Angebot.",
            response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Zusätze erfolgreich zurückgegeben.")
	})
	@RequestMapping(
	        path = "/api/additivesForOfferId/{offerId}",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<Additive> getAdditivesForOffer(
	        @PathVariable("offerId")
            @ApiParam(
                    value = "ID des Angebots, für das die Zusätze zu bestimmen sind.",
                    required = true)
            Integer offerId,
            HttpServletRequest request) {

		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		Offer offer = offerRepository.getOne(offerId);

		return offer.getAdditives();
	}
}
