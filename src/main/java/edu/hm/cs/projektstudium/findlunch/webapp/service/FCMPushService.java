package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * In der Klasse werden alle Operationen, die über Firebase Cloud Messaging abgewickelt werden sollen,
 * gesammelt. Die Methoden der Klasse formatieren den Inhalt der Nachrichten und leiten sie an den FCMClient
 * zum Versenden weiter.
 */
@Service
public class FCMPushService {

    /** Logger zum Protokollieren von Serverereignissen. */
    private final Logger LOGGER = LoggerFactory.getLogger(FCMPushService.class);

    /** FCM-Client zur Initialisierung der Verbindung zu Firebase. */
    private final FCMClient fcmClient;

    /**
     * Konstruktor, der den FCMClient per @Autowire einbindet.
     * @param fcmClient Der Firebase-Client zum Versenden der Nachrichten.
     */
    @Autowired
    public FCMPushService(FCMClient fcmClient) {
        this.fcmClient = fcmClient;
    }

    /**
     * Methode zum Versenden der täglich zweimal angestoßenen Push-Data-Nachricht an den Client.
     * Die Nachricht veranlasst beim Client das Übermitteln der aktuellen Positionsdaten per REST-Aufruf.
     * Weil die Nachricht unbemerkt vom Client in der App verarbeitet werden soll, wird eine Data-Message gesendet.
     * @param pushToken FCM-Token des Users
     */
    public void sendDailyPushNotification(String pushToken) {

        // "data" : - Werte für Nachricht in HashMap ablegen.
        Map<String, String> data = new HashMap<>();
        data.put("command", "SEND_LOCATION");
        try {
            // Nachricht als Data-Message senden.
            String result = fcmClient.sendDataMessage(pushToken, data);
            LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), result));
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage()));
        }
    }

    /**
     * Sendet eine Benachrichtigung, dass die Reservierung vom Restaurant noch nicht bearbeitet wurde.
     * @param reservation Reservierung
     * @param pushToken FCM-Token des Nutzers
     */
    public void sendReservationNotDonePush(Reservation reservation, String pushToken) {
        sendReservationPush(reservation, pushToken, "nicht rechtzeitig bearbeitet");
    }

    /**
     * Sendet eine Benachrichtigung, dass die Reservierung vom Restaurant bestätigt wurde.
     * @param reservation Reservierung
     * @param pushToken FCM-Token des Nutzers
     */
    public void sendReservationConfirmPush(Reservation reservation, String pushToken) {
        sendReservationPush(reservation, pushToken, "bestätigt");
    }

    /**
     * Sendet eine Benachrichtigung, dass die Reservierung vom Restaurant abgelehnt wurde.
     * @param reservation Reservierung
     * @param pushToken FCM-Token des Nutzers
     */
    public void sendReservationRejectPush(Reservation reservation, String pushToken) {
        sendReservationPush(reservation, pushToken, "abgelehnt. Grund: " + reservation.getReservationStatus());
    }

    /**
     * Methode zum Versenden von Notifikationen, die mit der Bestellung zusammenhängen.
     * Sie wird von den drei public-Methoden aufgerufen und setzt eine Textnachricht über den Status der Bestellung
     * zusammen.
     * @param reservation Reservierungs-/Bestellungsobjekt
     * @param pushToken FCM-Token des Nutzers
     * @param message Ende des Satzes
     */
    private void sendReservationPush(Reservation reservation, String pushToken, String message) {
        try {
            // Nachricht als Notification-Message senden.
            String result = fcmClient.sendNotificationMessage(pushToken,
                    "Deine Bestellung",
                    "Deine Bestellung " + reservation.getReservationNumber()+
                            " wurde durch das Restaurant "+ reservation.getRestaurant().getName()+
                            " " + message + ".");
            LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), result));
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage()));
        }
    }

    /**
     * Für Messungen mit dem Package "Measurement"
     * @param pushToken Der Push-Token
     * @param timestamp Zeitstempel des Versands
     * @param pushCount Push-Nummer
     * @return Ergebnis des Versands
     */
    public String sendMeasurePush(String pushToken, String timestamp, String pushCount) {

        String result = "";
        try {
            Map<String, String> data = new HashMap<>();
            data.put("timestamp", timestamp);
            data.put("pushCount", pushCount);
            // Nachricht als Data-Message senden.
            result = fcmClient.sendDataMessage(pushToken,
                    data);
            LOGGER.info(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(), result));
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(LogUtils.getDefaultSchedulerMessage(Thread.currentThread().getStackTrace()[1].getMethodName(),
                    e.getMessage()));
        }
        return result;
    }

    /**
     * Methode, um das Abfragen der Angebote in der Nähe manuell anzustoßen.
     *
     * @param pushToken Der FCM-Push-Token
     */
    public void sendTestNotification(String pushToken) {
        // Nachricht als Notification-Message senden.
        sendDailyPushNotification(pushToken);
    }
}