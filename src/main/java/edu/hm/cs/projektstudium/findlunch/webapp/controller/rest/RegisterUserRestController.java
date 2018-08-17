package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.CustomUserValidator;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserTypeRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * The class is responsible for handling rest calls related to registering users.
 */
@RestController
@Api(
        value="Registrierung",
        description="Benutzerregistrierung.")
public class RegisterUserRestController {

	/** The request. */
	private final HttpServletRequest request;

	/** The user repository. */
	private final UserRepository userRepository;

	/** The user type repository. */
	private final UserTypeRepository userTypeRepository;

	/** The bcrypt password encoder. */
	private final BCryptPasswordEncoder bcryptPasswordEncoder;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(RegisterUserRestController.class);

    @Autowired
    public RegisterUserRestController(HttpServletRequest request, UserRepository userRepository, UserTypeRepository userTypeRepository, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.request = request;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    /**
	 * Register a single user.
	 *
	 * @param request the HttpServletRequest
	 * @param user
	 *            the user that should be registered
	 * @return the response entity
	 */
	@CrossOrigin
	@ApiOperation(
	        value = "Benutzer registrieren.",
            response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Benutzer erfolgreich registriert."),
			@ApiResponse(code = 409, message = "Konflikt: Benutzername bereits vorhanden, E-Mail-Validierung " +
					"fehlgeschlagen oder Verstoß gegen Passwort-Richtlinien.")
	})
	@RequestMapping(
	        path = "/api/register_user",
            method = RequestMethod.POST,
            produces = "application/json")
	public ResponseEntity<Integer> registerUser(@RequestBody
        @ApiParam (
                value = "Benutzer",
                required = true)
        User user, HttpServletRequest request) {
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		//TODO Captcha - get Key from google (https://www.google.com/recaptcha)
		/*
		if (user.getCaptcha() == null || user.getCaptcha().getAnswer() == null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),
					"The Captcha: " + " was empty."));
			// Todo Ggf. Architekturdokument_public.pdf entsprechend neuem Statuscode anpassen
			NotificationController.sendMessageToTelegram("The CAPTCHA wasn't solved correctly in the mobile application."
					+ "The Captcha answer was: Empty" + " The IP of the user was: " + getClientIP());
			return new ResponseEntity<>(4, HttpStatus.CONFLICT);
		}

		//if (user.getCaptcha() == null || user.getCaptcha().getAnswer() == null) {
			if (!CaptchaController.verifyCaptcha(user.getCaptcha(), getClientIP())) {
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),
						"The Captcha: " +
								user.getCaptcha().getAnswer() + " was not solved correctly." +
								" Token: " + user.getCaptcha().getImageToken()));
				// Todo Ggf. Architekturdokument_public.pdf entsprechend neuem Statuscode anpassen
				NotificationController.sendMessageToTelegram("The CAPTCHA wasn't solved correctly in the mobile application."
						+ "The Captcha answer was: " + user.getCaptcha().getAnswer() + " The token was: "
						+ user.getCaptcha().getImageToken() + " The IP of the user was: " + getClientIP());
				return new ResponseEntity<>(4, HttpStatus.CONFLICT);
			}
		//}
		 */

		if (userRepository.findByUsername(user.getUsername()) != null) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(),"The user with username " + user.getUsername() + " could not be found in the databse."));
			return new ResponseEntity<>(3, HttpStatus.CONFLICT);
		}

		if (!CustomUserValidator.validateEmail(user.getUsername())) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The username " + user.getUsername() + " is not valid."));
			return new ResponseEntity<>(1, HttpStatus.CONFLICT);
		}

		if (!CustomUserValidator.checkPasswordRules(user.getPassword())) {
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The password rules for user " + user.getUsername() + " are not fullfilled."));
			return new ResponseEntity<>(2, HttpStatus.CONFLICT);
		}

		user.setUserType(userTypeRepository.findByName("Kunde"));
		user.setPassword(bcryptPasswordEncoder.encode(user.getPassword()));

		userRepository.save(user);

		return new ResponseEntity<>(0, HttpStatus.OK);

	}

	/**
	 * This method gets the client's IP-address and pays attention for the X-Forwarded-For header which could
	 * identify a proxy user. See for example: https://tools.ietf.org/html/rfc7239
	 *
	 * @return the client's IP-address
	 */
	private String getClientIP() {
		final String xffHeader = request.getHeader("X-Forwarded-For");
		if (xffHeader == null){
			return request.getRemoteAddr();
		}
		return xffHeader.split(",")[0];
	}

}
