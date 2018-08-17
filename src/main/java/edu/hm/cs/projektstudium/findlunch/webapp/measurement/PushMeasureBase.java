package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.service.FCMPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 
 * PushMeasureBase class (Main class for measurement!).
 * 
 * Separate base class for sending push notification measures (completely separated from live-operation).
 * **Live operation has to be disabled at package "scheduling", class "PushNotificationScheduledTask".
 * **Required measurement has to be enabled in this class.
 * 
 * Measures FCM push-notifications.
 * Can be used with other Google credentials than live-operation if required.
 * #Credentials configuration:
 * **Configure FCMadminkey.json, representing Google identification
 * 
 * #Measure configuration:
 * Uncomment annotation "@PostConstruct" or "@Scheduled" for executing current measure.
 * Only one measure possible at simultaneously.
 * 
 * Speed and performance measure of FCM require one registered user with username starting with "push@"
 * and pushNotificiationsEnabled = true and a valid push-token.
 * Can be specified for one or more users.
 * Scaled measure only possible with FCM service.
 * Scaled FCM measure, collect all pushes starting with "test@" of different users for multi-push-message measure.
 * 
 * UnitTest not suitable at this case, using PostConstruct for measures.
 * Uncomment for measure execution.
 * 
 * Created by Maxmilian Haag on 18.12.2016.
 * @author Maximilian Haag
 * 
 *
 */

@SuppressWarnings("unused")
@Component
public class PushMeasureBase implements PushMeasureInterface {

	/** 
	 * The logger. 
	 */
	private final Logger LOGGER = LoggerFactory.getLogger(PushMeasureBase.class);
	
	
	//measure adjustment (change here) #########################
	private final static int NUMBER_OF_PUSHES = 100;
	private final static int USER_DEVICES = 1;
	private final static int NUMBER_OF_ITERATIONS = 1;
	private final static boolean SORT_SCALED_MEASURE_WHEN_READY = false;
	//measure adjustment (change here) #########################
	

	/**
	 * Google / Amazon identification credentials read from /src/main/resources/MeasureCredentials.conf 
	 */
	protected static String FCM_SENDER_ID;

	/** User-Repository */
	private final UserRepository userRepository;

	/** Push-Token-Repository mit Token zur User-Id. */
	private final PushTokenRepository pushTokenRepository;

	/** Push-Service zum Versenden der Nachrichten */
	private final FCMPushService fcmPushService;

	/**
	 * Single threaded executor for sequency.
	 */
	private ExecutorService singleEx = Executors.newSingleThreadExecutor();
	
	/**
	 * Current sent number of pushes.
	 */
	private int pushCount = 0;
	


	/**
	 * Initialize base credentials only once for inherited runnables.
	 * @param userRepository User-Repository
	 * @param pushTokenRepository Push-Token-Repository
	 * @param fcmPushService FCM-Push-Service
	 */
	@Autowired
	public PushMeasureBase(UserRepository userRepository, PushTokenRepository pushTokenRepository, FCMPushService fcmPushService) {
		this.userRepository = userRepository;
		this.pushTokenRepository = pushTokenRepository;
		this.fcmPushService = fcmPushService;
	}


	/**
	 * ##############################################
	 * # UNCOMMENT "@PostConstruct" FOR MEASUREMENT!#
	 * ##############################################
	 * 
	 * Launches FCM performance measure for high message amount.
	 * Requires registred Google FCM push (FCM token). 
	 */
	//@PostConstruct
	public void launchPerformanceFCMMeasure() {
		LOGGER.info("Launching FCM Performance Measure");
		
		//Load dummy push notification from db
		String pushToken = extractFromDB();
		if(!pushToken.isEmpty()) {
			for (int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
				for (int i = 0; i < NUMBER_OF_PUSHES; i++) {
					singleEx.execute(new FcmPushMeasure(pushToken, fcmPushService));
				}
			}
		}
	}

	/**
	 * ##########################################
	 * # UNCOMMENT "@Scheduled" FOR MEASUREMENT!#
	 * ##########################################
	 * 
	 * Launches FCM single speed measure. 
	 * Long time measure for single sending and receiving of messages to evaluate single RTT speed.
	 * NOT for high amount of messages (use performance measure above).
	 * Benötigt einen registrierten Benutzer mit dem Usernamen "test@push.de" und pushNotificationsEnabled = true.
	 * Sending rate adjustable (current: each 10000 ms)
	 */
	//@Scheduled(fixedRate = 10000)
	public void launchSpeedFCMMeasure() {
		LOGGER.info("Launching FCM (Single) Speed Measure");

		//Load dummy push notification from db
		String pushToken = extractFromDB();
		
		if(pushCount < NUMBER_OF_PUSHES) {
			singleEx.execute(new FcmPushMeasure(pushToken, fcmPushService, pushCount));
			pushCount++;
		}
	}
	
	/**
	 * ##############################################
	 * # UNCOMMENT "@PostConstruct" FOR MEASUREMENT!#
	 * ##############################################
	 * 
	 * Launches scaled test for each "testpush" addressed on one or more different user ids.
	 * Requires registered users with username = "test@*" on different devices and with pushNotificationEnabled = true.
	 */
	//@PostConstruct
	public void launchDeviceScaledFCMMeasure() {
		LOGGER.info("Launching Device scaled FCM Measure");
		
		List<String> pushTokens = new ArrayList<>();

		//FCM Token extract from Database by registered name "testpush".
		List<User> users = userRepository.findAllByPushNotificationEnabledIsTrue();
		for(User user : users){
			if(user.getUsername().startsWith("test@")){
				PushToken pushToken = pushTokenRepository.findByUserId(user.getId());
				if(pushToken!=null){
					if(!pushToken.getFcmToken().isEmpty()){
						pushTokens.add(pushToken.getFcmToken());
					}
				}
			}
		}

		for(int j = 0; j < NUMBER_OF_ITERATIONS; j++) {
			for (int i = 0; i < NUMBER_OF_PUSHES; i++) {
				for (String token : pushTokens) {
					singleEx.execute(new FcmPushMeasure(token, fcmPushService, NUMBER_OF_PUSHES));
				}
			}
		}
	}
	
	/**
	 * Der User "test@push.de" muss vorab angelegt sein und ein gültiges FCM-Token besitzen.
	 * @return Das Token
	 */
	private String extractFromDB() {
		//Token extract from Database by registered name "testpush".
		User user = userRepository.findByUsernameAndUserType_name("test@push.de", "Kunde");
		if (user != null) {
			if (user.isPushNotificationEnabled()) {
				PushToken token = pushTokenRepository.findByUserId(user.getId());
				if (token != null) {
					return token.getFcmToken();
				}
			}
		}
		return "";
	}

	/**
	 * Current number of pushes.
	 * @return Current number of pushes.
	 */
	public static int getNumberOfPushes() {
		return NUMBER_OF_PUSHES;
	}



	/**
	 * Current number of operating devices.
	 * @return Current number of operating devices.
	 */
	public static int getUserDevices() {
		return USER_DEVICES;
	}



	/**
	 * Number of measure iterations.
	 * @return Number of measure iterations.
	 */
	public static int getNumberOfIterations() {
		return NUMBER_OF_ITERATIONS;
	}



	/**
	 * Sort measure if measure ready.
	 * @return Sort measure.
	 */
	public static boolean isSortMeasureWhenReady() {
		return SORT_SCALED_MEASURE_WHEN_READY;
	}
	
}


