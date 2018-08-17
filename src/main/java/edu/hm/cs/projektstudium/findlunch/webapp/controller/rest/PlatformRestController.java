package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.google.gson.JsonObject;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Platform;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Version;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PlatformRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.VersionRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * The class is responsible for handling API calls to update the platform statistics into the database.
 */
@RestController
@Api(value="Platform-Statistics", description="Update version statistics")
public class PlatformRestController {


	final PlatformRepository platformRepository;

	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);

	@Autowired
	public PlatformRestController(PlatformRepository platformRepository) {
		this.platformRepository = platformRepository;
	}

	/**
	 * Update Version statistics
	 *
	 * @param platformName the version name
	 * @param request   the http request
	 * @param principal the principal
	 * @return response entity representing a status code
	 */
	@ApiOperation(value = "Update platform statistics")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Platform statistics updated."),
			@ApiResponse(code = 202, message = "New Platform statistic initialized")
	})
	@RequestMapping(path = "api/platform/{platformName}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> trackVersion(
			@PathVariable
			@ApiParam(value = "Platform to be updated",
					required = true)
					String platformName,
			HttpServletRequest request,
			Principal principal) {

		JsonObject jsonObject = new JsonObject();

		Platform oldPlatform = platformRepository.findByPlatformName(platformName);
		// if there is no such platform stored
		if (oldPlatform == null) {
			Platform newPlatform = new Platform(platformName);
			newPlatform.setCount(1);
			platformRepository.save(newPlatform);

			jsonObject.addProperty("value", "New Platform entry created");

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.ACCEPTED);
		}
		// refresh the platform
		else {
			oldPlatform.setCount(oldPlatform.getCount()+1);
			platformRepository.save(oldPlatform);

			jsonObject.addProperty("value", "Value updated");

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}

    }
}
