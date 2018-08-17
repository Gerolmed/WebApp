package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferPhotoRepository;

/**
 * The class is responsible for handling rest calls related to offer photos.
 * Request param offer_id.
 */
@RestController
@Api(
        value="Bilder zum Angebot",
        description="Zum Abrufen von Fotos zum Angebot.")
public class OfferPhotoRestController {
	
	/** The offer photo repository. */
	private final OfferPhotoRepository offerPhotoRepo;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(OfferPhotoRestController.class);

    @Autowired
    public OfferPhotoRestController(OfferPhotoRepository offerPhotoRepo) {
        this.offerPhotoRepo = offerPhotoRepo;
    }

    /**
	 * Gets the offer photos for a given offer
	 *
	 * @param request the HttpServletRequest
	 * @param offerId the offer id
	 * @return the all photos for the given offer
	 */
	@CrossOrigin
	@JsonView(OfferView.OfferPhotoFull.class)
	@ApiOperation(
	        value = "Abrufen der Bilder zu einem Angebot.",
            response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Fotos erfolgreich abgerufen.")
	})
	@RequestMapping(
	        path = "/api/offer_photos",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<OfferPhoto> getOfferPhotos(
	        @RequestParam(
	                name = "offer_id")
            @ApiParam(
                    value = "ID des Angebots",
                    required = true)
            int offerId, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		System.out.println("OfferPhotoRestController");
		
		return offerPhotoRepo.findByOffer_id(offerId);
	}
	
	/**
	 * Exception handler for MethodArgumentTypeMismatchException.
	 *
	 * @param request the HttpServletRequest
	 * @param e the exception
	 * @return the name of the exception class
	 */
	@ExceptionHandler(value=MethodArgumentTypeMismatchException.class)
	public String exceptionHandler(Exception e, HttpServletRequest request){
		LOGGER.error(LogUtils.getExceptionMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), e));
		return e.getClass().toString();
	}
}
