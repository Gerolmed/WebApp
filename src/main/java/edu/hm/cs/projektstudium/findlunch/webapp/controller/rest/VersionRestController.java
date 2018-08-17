package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.google.gson.JsonObject;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Version;
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
 * The class is responsible for handling API calls to update the version statistics into the database.
 */
@RestController
@Api(value="Version-Statistics", description="Update version statistics")
public class VersionRestController {


	final VersionRepository versionRepository;

	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);

	@Autowired
	public VersionRestController(VersionRepository versionRepository) {
		this.versionRepository = versionRepository;
	}

	/**
	 * Update Version statistics
	 *
	 * @param version_name the version name
	 * @param request   the http request
	 * @param principal the principal
	 * @return response entity representing a status code
	 */
	@CrossOrigin
	@ApiOperation(value = "Update version statistics")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Version statistics updated."),
			@ApiResponse(code = 202, message = "New Version statistic initialized")
	})
	@RequestMapping(path = "api/version/{version_name}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> trackVersion(
			@PathVariable
			@ApiParam(value = "Version to be updated",
					required = true)
					String version_name,
			HttpServletRequest request,
			Principal principal) {

		JsonObject jsonObject = new JsonObject();

		Version oldVersion = versionRepository.findByVersionName(version_name);
		// if there is no such version stored
		if (oldVersion == null) {
			Version newVersion = new Version(version_name);
			newVersion.setCount(1);
			versionRepository.save(newVersion);

			jsonObject.addProperty("value", "New Version entry created");

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.ACCEPTED);
		}
		// refresh the version
		else {
			oldVersion.setCount(oldVersion.getCount()+1);
			versionRepository.save(oldVersion);

			jsonObject.addProperty("value", "Value updated");

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}

    }
}
