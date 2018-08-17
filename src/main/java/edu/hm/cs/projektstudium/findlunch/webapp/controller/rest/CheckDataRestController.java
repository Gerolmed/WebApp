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
 * The class is responsible for handling API calls to update the version statistics into the database.
 */
@RestController
@Api(value="Version-Statistics", description="Update version statistics")
public class CheckDataRestController {


	private PlatformRepository platformRepository;
	final VersionRepository versionRepository;

	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);

	@Autowired
	public CheckDataRestController(PlatformRepository platformRepository, VersionRepository versionRepository) {
		this.platformRepository = platformRepository;
		this.versionRepository = versionRepository;
	}

	/**
	 * http://localhost:8080/api/check_data?version_name=hello
	 *
	 * @param version_name the version name
     * @param platform_name the platform name
	 * @param request   the http request
	 * @return response entity representing a status code
	 */
	@CrossOrigin
	@ApiOperation(value = "http://localhost:8080/api/check_data?version_name=hello")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Version statistics updated."),
			@ApiResponse(code = 202, message = "New Version statistic initialized")
	})
	@RequestMapping(path = "api/check_data", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> trackVersion(
			@RequestParam String version_name,
			@RequestParam String platform_name,
			HttpServletRequest request) {

		JsonObject jsonObject = new JsonObject();

		//Update version counter and check version
        {
            Version oldVersion = versionRepository.findByVersionName(version_name);

            jsonObject.addProperty("validVersion", isValidVersion(version_name));

            // if there is no such version stored
            if (oldVersion == null) {
                Version newVersion = new Version(version_name);
                newVersion.setCount(1);
                versionRepository.save(newVersion);
            }
            // refresh the version
            else {
                oldVersion.setCount(oldVersion.getCount()+1);
                versionRepository.save(oldVersion);
            }
        }

        //Update platform counter and check platform
        {
            Platform oldPlatform = platformRepository.findByPlatformName(platform_name);

            jsonObject.addProperty("validPlatform", true);

            // if there is no such platform stored
            if (oldPlatform == null) {
                Platform newPlatform = new Platform(platform_name);
                newPlatform.setCount(1);
                platformRepository.save(newPlatform);

                jsonObject.addProperty("value", "New Platform entry created");
            }
            // refresh the platform
            else {
                oldPlatform.setCount(oldPlatform.getCount()+1);
                platformRepository.save(oldPlatform);

                jsonObject.addProperty("value", "Value updated");
            }
        }

		return  new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    /**
     * Checks if given version string is a compatible client version
     * @param version - version to be checked
     * @return - is client version compatible
     */
    private boolean isValidVersion(String version) {
	    return true;
    }


}
