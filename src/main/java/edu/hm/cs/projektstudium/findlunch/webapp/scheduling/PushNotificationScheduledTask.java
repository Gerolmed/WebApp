package edu.hm.cs.projektstudium.findlunch.webapp.scheduling;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.PushToken;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.PushTokenRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.service.FCMPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Klasse PushNotificationScheduledTask
 *
 * Zuständig für das Versenden der Push-Nachrichten an User, die zweimal täglich nahegelegene Angebote per
 * Push erhalten möchten. Die Push-Nachricht stößt dabei aber nur einen REST-Aufruf der Clients an und
 * übermittelt noch nicht die Angebote.
 */
@Component
public class PushNotificationScheduledTask {

    /** Logger zum Protokollieren von Serverereignissen. */
    private final Logger LOGGER = LoggerFactory.getLogger(FCMPushService.class);

    /** User-Repository */
    private final UserRepository userRepository;

    /** Push-Token-Repository mit Token zur User-Id. */
    private final PushTokenRepository pushTokenRepository;

    /** Push-Service zum Versenden der Nachrichten */
    private final FCMPushService fcmPushService;

    /** Identifikationsstring für fehlenden/abgelaufenen Token in der Datenbank. */
    private final static String NOT_AVAILABLE = "notAvailable";


    /**
     * Konstruktor, der die Repositories/Services per @Autowired einbindet.
     * @param userRepository User-Repository
     * @param pushTokenRepository Push-Token-Repository
     * @param fcmPushService FCM-Push-Service
     */
    @Autowired
    public PushNotificationScheduledTask(UserRepository userRepository, PushTokenRepository pushTokenRepository, FCMPushService fcmPushService) {
        this.userRepository = userRepository;
        this.pushTokenRepository = pushTokenRepository;
        this.fcmPushService = fcmPushService;
    }

    /**
     *  Methode, die um 11 und 17 Uhr die User, bei denen pushNotificationEnabled = true ist, auswählt und veranlasst,
     *  dass ihnen eine Push-Nachricht per Firebase gesendet wird.
     */
    @Scheduled(cron = "0 0 11,17 * * ?", zone = "CET")
    public void sendDailyPushNotifications() {

        LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                "Beginne mit Versand der täglichen Push-Nachrichten."));

        // User mit gewünschter Push-Notifikation ermitteln.
        List<User> users = userRepository.findAllByPushNotificationEnabledIsTrue();
        for(User user : users){
            // Push-Token-Objekt zu jedem Nutzer suchen.
            PushToken pushToken = pushTokenRepository.findByUserId(user.getId());
            // Wenn ein dazugehöriger Push-Token, der gültig ist, gefunden wurde, wird die Push-Nachricht erstellt und
            // versendet.
            if(pushToken!=null){
                if(!pushToken.getFcmToken().equals(NOT_AVAILABLE)) {
                    fcmPushService.sendDailyPushNotification(pushToken.getFcmToken());
                } else {
                    LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                            "Push-Token für User mit ID " + user.getId() + " nicht verfügbar, obwohl er Push-Nachrichten erhalten will."));
                }
            } else {
                LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                        "Push-Token für User mit ID " + user.getId() + " nicht existent, obwohl er Push-Nachrichten erhalten will."));
            }
        }
        LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                "Nachrichtenversand für " + users.size() + " User abgeschlossen."));
    }
}
