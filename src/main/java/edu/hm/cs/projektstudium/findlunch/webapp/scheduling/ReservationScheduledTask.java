package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.BraintreeController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.ReservationStatusRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.service.FCMPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Class that regularly checks for unprocessed reservations.
 *
 */
@Component
public class ReservationScheduledTask {
	
	/**
	 * The logger.
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationScheduledTask.class);
	
	/** The reservation repository. */
	private final ReservationRepository reservationRepository;
	
	/** The reservationStatus repository. */
	private final ReservationStatusRepository reservationStatusRepository;

	/** Push-Token-Repository mit Token zur User-Id. */
	private final PushTokenRepository pushTokenRepository;

	/** Push-Service zum Versenden der Nachrichten */
	private final FCMPushService fcmPushService;

	@Autowired
	public ReservationScheduledTask(ReservationRepository reservationRepository, ReservationStatusRepository reservationStatusRepository, PushTokenRepository pushTokenRepository, FCMPushService fcmPushService) {
		this.reservationRepository = reservationRepository;
		this.reservationStatusRepository = reservationStatusRepository;
		this.pushTokenRepository = pushTokenRepository;
		this.fcmPushService = fcmPushService;
	}

	/**
	 * Checks for unprocessed reservations every 3.5 minutes.
	 * If unprocessed reservations are found, they are saved and a push notification is sent out.
	 */
	@Scheduled(fixedRate = 200000)
	public void checkReservations() {
		//Log info
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),"Starting check for unprocessed reservations."));
		
		Date now = new Date();
		
		List<Reservation> reservations = reservationRepository.findAll();
		
		for(Reservation reservation : reservations){

			if(now.after(reservation.getCollectTime())&&reservation.getReservationStatus().getKey()==ReservationStatus.RESERVATION_KEY_NEW){
				reservation.setReservationStatus(reservationStatusRepository.findById(9));

				//Voids payment authorization if reservation was paid via PayPal. Tries 4 times if it fails.
				int i = 0;
				while (!BraintreeController.voidTransaction(reservation) && i < 4){
					i++;
				}
				reservationRepository.save(reservation);
				User user = reservation.getUser();
				PushToken userToken = pushTokenRepository.findByUserId(user.getId());
				// Wenn ein FCM-Token vorhanden ist, wird eine Push-Nachricht versendet.
				if(userToken != null) {
					fcmPushService.sendReservationNotDonePush(reservation, userToken.getFcmToken());
				}

			}
			
		}
		//Console log info
		LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
						"Check for unprocessed reservations finished."));
	}
}
