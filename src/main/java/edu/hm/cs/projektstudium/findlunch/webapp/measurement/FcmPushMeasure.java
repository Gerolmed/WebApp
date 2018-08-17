package edu.hm.cs.projektstudium.findlunch.webapp.measurement;

import edu.hm.cs.projektstudium.findlunch.webapp.service.FCMPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The FcmPushMeasure, sends Google FCM based push-notifications for measurement (not at live-operation).
 * 
 * Creates push-data-notification with timestamp and pushCount.
 *
 * Created by Maxmilian Haag on 20.12.2016 / 07.02.2017
 * @author Maximilian Haag
 *
 */
public class FcmPushMeasure implements Runnable {

	private final String pushToken;
	/**
	 * The logger. 
	 */
	private Logger LOGGER = LoggerFactory.getLogger(FcmPushMeasure.class);

	private final FCMPushService fcmPushService;

	private int pushCount = 0;

	/**
	 * Initialization of measure with push-notification to be sent and current count.
	 * @param pushToken Push-Token des Clients
	 * @param fcmPushService Push-Service des Firebase-Dienstes
	 */
	public FcmPushMeasure(String pushToken, FCMPushService fcmPushService) {
		this.pushToken = pushToken;
		this.fcmPushService = fcmPushService;
	}

	public FcmPushMeasure(String pushToken, FCMPushService fcmPushService, int pushCount) {
		this.pushToken = pushToken;
		this.fcmPushService = fcmPushService;
		this.pushCount = pushCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		LOGGER.info("PUSH-Measure: " +
				fcmPushService.sendMeasurePush(pushToken,
						String.valueOf(System.currentTimeMillis()),
						String.valueOf(pushCount)));
	}
}
