package edu.hm.cs.projektstudium.findlunch.webapp.service;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ResetPasswordRepository;

/**
 * The class is responsible for sending emails to the customers and restaurants.
 * 
 * @author Deniz Mardin, Niklas Klotz
 *
 */
@Service
public class MailService {

	/** 
	 * The MailSender.
	 * */
	@Autowired
	private JavaMailSender javaMailSender;
	
	/** 
	 * The resetpassword repository.
	 * */
	@Autowired
	ResetPasswordRepository resetPasswordRepository;
	
	/**
	 * Sends a mail for a new reservation.
	 * @param restaurant the restaurant
	 * @param reservatin the reservation
	 * @param url the url to the reservation
	 */
	public void sendNewReservationMail(Restaurant restaurant, Reservation reservatin, String url) {
		SimpleMailMessage mail = configureReservtionMail(restaurant, reservatin, url);
		javaMailSender.send(mail);
	}
	
	/**
	 * Sends a mail to reset the password.
	 * @param user the user
	 * @param resetLink a link to reset the password
	 * @throws MailException if the underlying service fails.
	 */
	public void sendResetPwMail(User user, String resetLink) throws MailException{
		SimpleMailMessage mail = configurePasswordMail(user, resetLink);
		javaMailSender.send(mail);
	}
	
	/**
	 * Builds the content of a new reservation mail.
	 * @param restaurant the restaurant
	 * @param reservation the reservation
	 * @param url the url to the reservation
	 * @return returns the mail
	 */
	private SimpleMailMessage configureReservtionMail(Restaurant restaurant, Reservation reservation, String url) {
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(restaurant.getEmail());
		mail.setSubject(messages.getString("reservation.title"));
		String text = MessageFormat.format(messages.getString("reservation.text"), url);
		mail.setText(text);
		return mail;
		
	}
	
	/**
	 * Builds a password mail.
	 * @param user the user
	 * @param resetLink link to reset the password
	 * @return returns the mail
	 */
	private SimpleMailMessage configurePasswordMail(User user, String resetLink){
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.text"), user.getUserType().getName(), resetLink);
		mail.setText(text);
		return mail;
	}
	
	/**
	 * Gets the rescources for the mail.
	 * @return
	 */
	private ResourceBundle getResurceBundel(){
		Locale currentLocale;
        currentLocale = new Locale("de", "DE");
        ResourceBundle messages = ResourceBundle.getBundle("messages.email", currentLocale);
		return messages;
	}
	
	/**
	 * Sets the token for the password reset.
	 * @param user the user
	 */
	public void sendPasswordRestToken(User user){
		SimpleMailMessage mail = configureCustomerPasswordMail(user);
		javaMailSender.send(mail);
		
	}

	/**
	 * Builds a reset mail for a customer.
	 * @param user the customer
	 * @return the mail 
	 */
	private SimpleMailMessage configureCustomerPasswordMail(User user) {
		ResourceBundle messages = getResurceBundel();
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(user.getUsername());
		mail.setSubject(messages.getString("resetpassword.title"));
		String text = MessageFormat.format(messages.getString("resetpassword.customer.text"), user.getResetPassword().getToken());
		mail.setText(text);
		return mail;
	}
}
