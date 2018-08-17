package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.service.MailService;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ResetPassword;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ResetPasswordRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The Class is responsible for handling API calls to reset the password.
 * 
 * @author Deniz Mardin
 *
 */
@RestController
@Api(
		value="Passwort-Zurücksetzung",
		description="Verarbeitung von Passwort-Zurücksetzen-Anfragen.")
public class ResetPasswordRestController {

	/**
	 * The user repository
	 */
	final UserRepository userRepository;
	
	/**
	 * The reset password repository
	 */
	private final ResetPasswordRepository resetPasswordRepository;
	
	/**
	 * The mail service
	 */
	private final MailService mailService;
	
	/**
	 * The password encoder
	 */
	private final BCryptPasswordEncoder passwordEncoder;
	
	/**
	 * http string
	 */
	private static final String HTTP = "http://";
	
	/**
	 * https string
	 */
	private static final String HTTPS= "https://";
	
	/**
	 * The logger
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordRestController.class);


	/**
	 * The controller to reset a users password.
	 * 
	 * @param userRepository the user repository
	 * @param resetPasswordRepository the reset password repository
	 * @param mailService the mail service
	 * @param passwordEncoder the password encoder
	 */
	@Autowired
	public ResetPasswordRestController(UserRepository userRepository, ResetPasswordRepository resetPasswordRepository, MailService mailService, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.resetPasswordRepository = resetPasswordRepository;
		this.mailService = mailService;
		this.passwordEncoder = passwordEncoder;
	}

	@CrossOrigin
	@ApiOperation(
			value = "Token für Passwort-Zurücksetzung beantragen.",
			response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Token erfolgreich beantragt.")
	})
	@RequestMapping(
			path ="api/get_reset_token",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<Integer> getResetPassword(HttpServletRequest request,
			@RequestBody
			@ApiParam(
					value = "Benutzername",
					required = true)
			String userName){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User u = userRepository.findByUsername(userName);
		if(u == null){
			LOGGER.info(LogUtils.getErrorMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Not existed Username(E-Mail) was entered."));
			//send always a success Mail because of IT-Sec reasons.Also add the difference of the needed time to send a Mail.
			return new ResponseEntity<>(0, HttpStatus.OK);
		}
		ResetPassword resetPasswordLast = resetPasswordRepository.findByUser(u);
		
		
		//try to reset pw more then one time in 24hours
		if(resetPasswordLast != null && validatePasswordDate(resetPasswordLast.getDate())){
			String resetLink = getPasswordResetUrl(request, u);
			try{
				mailService.sendResetPwMail(u,resetLink);
				LOGGER.info("Mail zum wiederholten Mal versendet!");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ResponseEntity<>(0, HttpStatus.OK);
		}
		
		//first try to reset pw in 24h
		ResetPassword resetPassword = new ResetPassword();

		Date date = java.sql.Date.valueOf(LocalDate.now(ZoneId.of("Europe/Berlin")));
		resetPassword.setDate(date);
		resetPassword.setToken(UUID.randomUUID().toString());
		resetPassword.setUser(u);
		u.setResetPassword(resetPassword);
		resetPasswordRepository.save(resetPassword);
		userRepository.save(u);
		String resetLink = getPasswordResetUrl(request, u);
		try{
			mailService.sendResetPwMail(u,resetLink);
			LOGGER.info("Mail zum ersten Mal versendet!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(0, HttpStatus.OK);
		
		/*
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		User user = userRepository.findByUsername(u.getUsername());
		if(user !=null){
			ResetPassword resetPassword = new ResetPassword();
			resetPassword.setDate(new Date());
			resetPassword.setToken(UUID.randomUUID().toString());
			resetPassword.setUser(user);
			user.setResetPassword(resetPassword);
			resetPasswordRepository.save(resetPassword);
			userRepository.save(user);
			
			mailService.sendPasswordRestToken(user);
			return new ResponseEntity<>(0, HttpStatus.OK);
		}
		
		//should we send Http code 200 because of itsec
//		return new ResponseEntity<Integer>(1, HttpStatus.CONFLICT);
		return new ResponseEntity<>(0, HttpStatus.OK);*/
	}
	
	/**
	 * Validates the password date.
	 * 
	 * @param dateToValidate 
	 * @return if the date of the password is valid.
	 */
	private boolean validatePasswordDate(Date dateToValidate){
		LocalDateTime dtv = LocalDateTime.ofInstant(dateToValidate.toInstant(), ZoneId.systemDefault());
		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		return dtv.isAfter(yesterday);
	}
	
	/**
	 * Gets the url to reset the password.
	 * 
	 * @param request the HttpServletRequest
	 * @param user the user
	 * @return a url to reset the password
	 */
	private String getPasswordResetUrl(HttpServletRequest request, User user) {
		return getProtocol(request.isSecure()) + request.getServerName()+":"+request.getServerPort()+"/resetpassword/"+user.getResetPassword().getToken();
	}
	
	/**
	 * Gets the used protocol.
	 * 
	 * @param https the https protocol
	 * @return the used protocol
	 */
	private String getProtocol(boolean https){
		return https ? HTTPS : HTTP;
	}

	@CrossOrigin
	@ApiOperation(
			value = "Passwort zurücksetzen.",
			response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Passwort erfolgreich zurückgesetzt.")
	})
	@RequestMapping(
			path ="api/reset_password/{token}",
			method = RequestMethod.PUT,
			produces = "text/html")
	public ResponseEntity<Integer> resetPassword(
			@PathVariable("token")
            @NotNull
			@ApiParam(
            		value = "Token",
                    required = true)
            String token,
			@RequestBody
			@ApiParam (
				value = "Benutzer",
				required = true)
			User u, HttpServletRequest request){
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		ResetPassword rp = resetPasswordRepository.findByToken(token);
		if(rp !=null){
			User user = rp.getUser();
			if(u.getPassword() != null  && u.getPasswordconfirm() != null){
				if(user != null && u.getPassword().equals(u.getPasswordconfirm())){
					user.setPassword(passwordEncoder.encode(u.getPassword()));
					user.setPasswordconfirm(passwordEncoder.encode(u.getPasswordconfirm()));
					userRepository.save(user);
					resetPasswordRepository.delete(rp);
					LOGGER.info("Gelöscht");
					return new ResponseEntity<>(HttpStatus.OK);
				}
			}
		}
		//should we send Http code 200 because of itsec
//		return new ResponseEntity<Integer>(1, HttpStatus.CONFLICT);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
