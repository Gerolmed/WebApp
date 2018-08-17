package edu.hm.cs.projektstudium.findlunch.webapp.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.RestaurantController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Initialisiert die Verbindung zum Firebase-Server, indem die JSON-Datei unter resources eingebunden wird.
 * Sendet Data- und Notification-Messages für einzelne Clients an Firebase.
 */
@Service
public class FCMClient {

    /** Ressource-Loader für den Zugriff auf Dateien aus der JAR-Datei heraus. */
    private ResourceLoader resourceLoader;

    /** Logger zum Protokollieren von Serverereignissen. */
    private final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    /**
     * Konstruktor, der die Verbindung zum Firebase-Server initiiert, indem die Einstellungen aus der JSON-Datei
     * "FCMadminkey.json" zum Starten einer Firebase-App genutzt werden.
     *
     * @param resourceLoader Wird per @Autowired eingebunden.
     */
    @Autowired
    public FCMClient(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

        Resource resource = this.resourceLoader.getResource("classpath:FCMadminkey.json");
            try {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                        .build();
                FirebaseApp.initializeApp(options);

            LOGGER.info("FIREBASE INITIALISIERT: " + FirebaseApp.getInstance().toString());
        } catch (Exception e) {
            LOGGER.error(LogUtils.getExceptionMessage("Constructor of FCMClient", e));
        }
    }

    /**
     * Sendet asynchron eine Datennachricht. Diese wird beim Client nicht im Sperrbildschirm angezeigt, sondern
     * von der App verarbeitet.
     * @param pushToken Token des Nutzers, an den die Nachricht von Firebase gesendet werden soll.
     * @param data Daten, die in der Nachricht gesendet werden sollen.
     * @return Erfolg des Versands
     * @throws InterruptedException Bei Abbruch der Verbindung
     * @throws ExecutionException Bei Fehlern während der Ausführung
     */
    String sendDataMessage(String pushToken, Map<String, String> data)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .putAllData(data)
                .setToken(pushToken)
                .build();

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    /**
     * Sendet asynchron eine Notification-Nachricht. Diese wird beim Client im Sperrbildschirm angezeigt.
     * @param pushToken Token des Nutzers, an den die Nachricht von Firebase gesendet werden soll.
     * @param title Titel der Push-Benachrichtigung
     * @param body Text der Push-Benachrichtigung
     * @return Erfolg des Versands
     * @throws InterruptedException Bei Abbruch der Verbindung
     * @throws ExecutionException Bei Fehlern während der Ausführung
     */
    String sendNotificationMessage(String pushToken, String title, String body)
            throws InterruptedException, ExecutionException {
        Message message = Message.builder()
                .setNotification(new Notification(title, body))
                .setToken(pushToken)
                .build();

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }
}

