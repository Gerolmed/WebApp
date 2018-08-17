package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.braintreegateway.PaymentMethodNonce;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.BraintreeController;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.service.MailService;
import edu.hm.cs.projektstudium.findlunch.webapp.model.EuroPerPoint;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Offer;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PointId;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Points;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationOffers;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Restaurant;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.EuroPerPointRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.OfferRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PointsRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.RestaurantRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * The class is responsible for handling rest calls related to registering users.
 *
 */
@RestController
@Api(
		value="Reservierungen",
		description="Verwaltung von Reservierungen.")
public class ReservationRestController {

	/** The restaurant repository. */
	private final UserRepository userRepository;
	
	/** The offer repository. */
	private final OfferRepository offerRepository;
	
	/** The reservation repository. */
	private final ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	private final ReservationStatusRepository reservationStatusRepository;
	
	/** The euroPerPoint repository. */
	private final EuroPerPointRepository euroPerPointRepository;
	
	/** The points repository. */
	private final PointsRepository pointsRepository;
	
	/** The restaurant repository. */
	private final RestaurantRepository restaurantRepository;

	/** The token repository. */
	private final PushTokenRepository tokenRepository;

	/**
	 * The mail service.
	 */
	private final MailService mailService;
	
	/**
	 * http string
	 */
	private static final String HTTP = "http://";
	
	/**
	 * https string
	 */
	private static final String HTTPS= "https://";
	
	/** The Logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationRestController.class);

	@Autowired
	public ReservationRestController(UserRepository userRepository, OfferRepository offerRepository, ReservationRepository reservationRepository, ReservationStatusRepository reservationStatusRepository, EuroPerPointRepository euroPerPointRepository, PointsRepository pointsRepository, RestaurantRepository restaurantRepository, PushTokenRepository tokenRepository, MailService mailService) {
		this.userRepository = userRepository;
		this.offerRepository = offerRepository;
		this.reservationRepository = reservationRepository;
		this.reservationStatusRepository = reservationStatusRepository;
		this.euroPerPointRepository = euroPerPointRepository;
		this.pointsRepository = pointsRepository;
		this.restaurantRepository = restaurantRepository;
		this.tokenRepository = tokenRepository;
		this.mailService = mailService;
	}

	/**
	 * Register a reservation.
	 * @param reservation Reservation to register
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Reservierung registrieren.",
	        response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Reservierung erfolgreich."),
			@ApiResponse(code = 401, message = "Nicht autorisiert."),
			@ApiResponse(code = 409, message = "Reservierung nicht erfolgreich.")
	})
	@RequestMapping(
			path= "api/register_reservation",
			method = RequestMethod.POST)
	public ResponseEntity<Integer> registerReservation(
			@RequestBody
			@ApiParam(
					value = "Reservierung",
					required = true)
			Reservation reservation, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		float calculatedPrice = 0;
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());
		
		List<ReservationOffers> reservation_Offers = reservation.getReservation_offers();
		
		Restaurant restaurant = null;

		// Bestellung entält keine Angebote
		if(reservation_Offers.isEmpty()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation does not contain any offer"));
			return new ResponseEntity<>(1, HttpStatus.CONFLICT);
		}
		LOGGER.info("Angebote vorhanden.");
		// Kein Abholzeitpunkt angegeben
		if(null == reservation.getCollectTime()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The Reservation has no CollectTime set"));
			return new ResponseEntity<>(9, HttpStatus.CONFLICT);
		}
		LOGGER.info("Abholzeitpunkt angegeben.");
		// Abholzeitpunkt liegt in der Vergangenheit
		if(reservation.getCollectTime().before(new Date())){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "The Reservation CollectTime is set in past"));
			return new ResponseEntity<>(10, HttpStatus.CONFLICT);
		}
		LOGGER.info("Abholzeitpunkt liegt in der Zukunft.");
		for(ReservationOffers reservation_offer : reservation_Offers) {
			LOGGER.info("Angebot:");
			// Bestellte Menge des Angebots ist 0 oder kleiner
			if(reservation_offer.getAmount() <= 0){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation has no amount for offer "+reservation_offer.getOffer().getId()));
				return new ResponseEntity<>(2, HttpStatus.CONFLICT);
			}
			LOGGER.info("-Bestellte Menge > 0");
			Offer offer = offerRepository.findOne(reservation_offer.getOffer().getId());

			// Angebots ID ist nicht in der DB
			if(offer==null){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "No Offer for ID "+reservation_offer.toString()));
				return new ResponseEntity<>(4, HttpStatus.CONFLICT);
			}
			LOGGER.info("-Angebots-ID existiert");
			// Angebot ist ausverkauft
			if(offer.isSoldOut()){
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Das Offer "+reservation_offer.getOffer().getId()+" is sold out"));
				return new ResponseEntity<>(5, HttpStatus.CONFLICT);
			}
			LOGGER.info("-Angebot ist nicht ausverkauft");
			//Vergleicht Restaurant des Angebots mit dem des vorherigen Angebots in der Bestellung
			if(restaurant!=null){
				if(restaurant.getId() != offer.getRestaurant().getId()){
					LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "An offer of the reservation is from another restaurant"));
					return new ResponseEntity<>(7, HttpStatus.CONFLICT);
				}
			}
			LOGGER.info("-Restaurant ist noch dasselbe.");
			//Restaurant wird auf das des aktuellen Angebots gesetzt, um es mit dem Restaurant des nächsten Angebots zu vergleichen.
			restaurant = offer.getRestaurant();

			reservation_offer.setReservation(reservation);
			
			calculatedPrice += reservation_offer.getAmount() * offer.getPrice();
			
		}
		LOGGER.info("--Ende der Angebotsprüfung--");
		
		// Der Gesamtpreis, welcher in der Customer App berechnet wurde stimmt nicht
		if(calculatedPrice+reservation.getDonation()+reservation.getFee()!=reservation.getTotalPrice()){
			LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Reservation price is incorrect"));
			return new ResponseEntity<>(6, HttpStatus.CONFLICT);
		}
		LOGGER.info("Gesamtpreis stimmt");
		// Authorization process for PayPal Transaction
		if(reservation.isUsedPaypal()){
			reservation.setPpTransactionId("ID");
	// Braintree Gateway (used for PayPal Transaction) only accepts BigDecimal data type
//			BigDecimal price = new BigDecimal(calculatedPrice);
//			BigDecimal percentageFee = price.multiply(BraintreeController.getPercentageFee());
//			price = price.add(percentageFee);
//			price = price.add(new BigDecimal(reservation.getDonation()));
//			price = price.add(BraintreeController.getFixedFee());
//			price = price.setScale(2, RoundingMode.HALF_DOWN);
//			// Create and send Transaction Request
//			TransactionRequest payPalRequest = new TransactionRequest()
//					.amount(price)
//					.paymentMethodNonce(reservation.getNonce())
//					.options()
//					.paypal()
//					.done()
//					.done();
//			Result<Transaction> saleResult = BraintreeController.gateway.transaction().sale(payPalRequest);
//			if (true) {
//				Transaction payPalTransaction = saleResult.getTarget();
//
//				// Set Transaction ID in reservation. Needed to void or settle reservation later.
//				reservation.setPpTransactionId(payPalTransaction.getId());
//				LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()), "Success: " + payPalTransaction.getId());
//			} else {
//				LOGGER.error(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()), "Failure: " + saleResult.getMessage());
//				return new ResponseEntity<Integer>(11, HttpStatus.CONFLICT);
//			}
//			LOGGER.info("PayPal-Zahlung korrekt");
		}
		EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1); //holt den euro pro punkt mit der id
		
		reservation.setReservationNumber(generateReservationNumber());
		reservation.setReservationStatus(reservationStatusRepository.findById(0));
		reservation.setTimestampReceived(new Date());
		reservation.setUser(authenticatedUser);
		reservation.setReservation_offers(reservation_Offers);
		reservation.setRestaurant(restaurant);
		reservation.setEuroPerPoint(euroPerPoint);
		reservation.setPointsCollected(false);
		
		if(!reservation.isUsedPoints()){
			reservation.setTotalPrice(calculatedPrice);
		}
		else{
			reservation.setTotalPrice(0f);
		}
		
		reservationRepository.save(reservation);
		
		String url = getProtocol(request.isSecure()) + request.getServerName()+":"+request.getServerPort() + "/reservations";
		
		// das Restaurant wird per Mail über die neue Reservierung benachrichtigt.
		mailService.sendNewReservationMail(restaurant, reservation, url);
		
		if(reservation.isUsedPoints()){
			//Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurant().getId());
			PointId pointId = new PointId();
			pointId.setUser(authenticatedUser);
			pointId.setRestaurant(restaurant);
			
			Points points = pointsRepository.findByCompositeKey(pointId);
			int usablePoints = points.getPoints();
			int neededPoints = getNeededPoints(reservation_Offers);
			if(usablePoints >= neededPoints){
				points.setPoints(usablePoints - neededPoints);
				pointsRepository.save(points);
				return new ResponseEntity<>(0, HttpStatus.OK);
			}
			else {
				//punkte reichen nicht
				LOGGER.error(LogUtils.getErrorMessage(request, Thread.currentThread().getStackTrace()[1].getMethodName(), "Points not enough. Needed: " + neededPoints + ", Usable: " + usablePoints));
				return new ResponseEntity<>(8, HttpStatus.CONFLICT);
			}
			
		}else{
			return new ResponseEntity<>(0, HttpStatus.OK);
		}
	}
	
	
	/**
	 * Collects the points for a given user.
	 * @param restaurantUuid UUID from Restaurant
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@ApiOperation(
			value = "Reservierung bestätigen.",
	        response = Integer.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Reservierung erfolgreich bestätigt."),
			@ApiResponse(code = 401, message = "Nicht autorisiert."),
			@ApiResponse(code = 409, message = "Reservierung konnte nicht bestätigt werden.")
	})
	@RequestMapping(
			path = "api/confirm_reservation/{restaurantUuid}",
			method = RequestMethod.PUT,
			produces = "application/json")
	public ResponseEntity<Integer> confirmReservation(
			@PathVariable("restaurantUuid")
            @ApiParam(
					value = "ID des Restaurants, welches die Reservierung bestätigen soll.",
					required = true)
            String restaurantUuid, Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());
		
		Restaurant r = restaurantRepository.findByRestaurantUuid(restaurantUuid);
		if(r==null){
			//restaurant nicht gefunden
			return new ResponseEntity<>(3, HttpStatus.CONFLICT);
		}
		
		LocalDateTime midnight = LocalDate.now().atStartOfDay();
		Date startOfDay = Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
		List<Reservation> reservations = reservationRepository.findByUserIdAndTimestampReceivedAfterAndReservationStatusKeyAndPointsCollectedFalse(authenticatedUser.getId(), startOfDay, ReservationStatus.RESERVATION_KEY_CONFIRMED);
		
		if(!reservations.isEmpty()){
			for(Reservation reservation : reservations){
				
				
					EuroPerPoint euroPerPoint = euroPerPointRepository.findOne(1);
					Double amountOfPoints = reservation.getTotalPrice() * euroPerPoint.getEuro();

					PointId pointId = new PointId();
					pointId.setUser(authenticatedUser);
					pointId.setRestaurant(reservation.getRestaurant());
						
					Points points = pointsRepository.findByCompositeKey(pointId);
					if(points == null){ //user get First time points
						points = new Points();
						points.setCompositeKey(pointId);
						points.setPoints(amountOfPoints.intValue());
					}
					else{//add new points to the old points
						points.setPoints(points.getPoints() +amountOfPoints.intValue());
					}
					reservation.setPointsCollected(true);
					reservation.setPoints(amountOfPoints.intValue());
					reservation.getFee();
					reservationRepository.save(reservation);
					pointsRepository.save(points);
			}
			return new ResponseEntity<>(0, HttpStatus.OK);
		}
		else{
			//keine Reservierung
			return new ResponseEntity<>(4, HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Get reservations for the logged in user.
	 * @param principal the principal to get the authenticated user
	 * @param request the HttpServletRequest
	 * @return the response entity representing a status code
	 */
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@JsonView(ReservationView.ReservationRest.class)
	@ApiOperation(
			value = "Reservierungen zum angemeldeten Benutzer abrufen.",
			response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Reservierungen zum Benutzer erfolgreich zurückgegeben."),
			@ApiResponse(code = 401, message = "Nicht autorisiert.")
	})
	@RequestMapping(
			path = "api/getCustomerReservations",
			method = RequestMethod.GET,
			produces = "application/json")
	public ResponseEntity<List<Reservation>> getUserCustomerReservations(Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		
		User authenticatedUser = (User) ((Authentication) principal).getPrincipal();
		authenticatedUser = userRepository.findOne(authenticatedUser.getId());
	
		List<Reservation> reservations = reservationRepository.findByUserIdOrderByRestaurantIdAscTimestampReceivedAsc(authenticatedUser.getId());
		
		return new ResponseEntity<>(reservations, HttpStatus.OK);
	}
	
    /**
     * Generate an unique reservation number for the reservation.
     * @return reservation number
     */
	//See Stackoverflow: http://stackoverflow.com/questions/12659572/how-to-generate-a-random-9-digit-number-in-java
	private int generateReservationNumber(){
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String rN = Long.toString(midSeed).substring(0, 9);
        return Integer.parseInt(rN);
    }
	
	private int getNeededPoints(List<ReservationOffers> reservation_Offers){
		
		int neededPoints = 0;
		
		for(ReservationOffers reOffers : reservation_Offers){
			Offer offer = offerRepository.findById(reOffers.getOffer().getId());
			neededPoints += reOffers.getAmount() * offer.getNeededPoints();
		}
		
		return neededPoints;
	}



	@ApiOperation(
			value = "Abholen des Braintree Tokens zum Zahlen mit PayPal.",
			response = String.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Token erfolgreich zurückgegeben."),
			@ApiResponse(code = 401, message = "Nicht autorisiert.")
	})
	@CrossOrigin
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(path = "api/paypal/get_token",
			method = RequestMethod.GET)
	public ResponseEntity<String> acquirePayPalToken(Principal principal, HttpServletRequest request){
		LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

		String token = BraintreeController.gateway.clientToken().generate();
		return new ResponseEntity<String>(token, HttpStatus.OK);
	}

	//get Protocol for the email
	private String getProtocol(boolean https){
		return https ? HTTPS : HTTP;
	}
}
