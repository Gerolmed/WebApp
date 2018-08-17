package edu.hm.cs.projektstudium.findlunch.webapp.controller;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is used for notifying the people responsible through an instant messenger.
 * The methods of this class will be used in case of potential security breaches.
 * The instant messenger used in this scenario should be running on a mobile device, like a mobile phone or a tablet.
 * <p>
 * The NotificationController is used in the following scenarios: <p>
 * 1. A session-ID is blocked because of a potential brute-force-attack.<p>
 * 2. A session-ID and IP-address is blocked because of a potential brute-force-attack.<p>
 * 3. A password doesn't match the password policy.<p>
 * 4. A potential brute-force-attack against the RESTful-webservice was successful, despite some values were blocked. <p>
 * 5. A potential brute-force-attack against the website was blocked due to an error page instead of the common
 *    login page.<p>
 * 6. A Captcha was solved incorrectly on the mobile application.<p>
 * 7. A Captcha was solved incorrectly on the website.<p>
 * 8. When someone tries to upload more files than allowed.<p>
 * 9. When someone tries to upload a file bigger than ten megabyte.<p>
 * 10. When there is a suspicious activity regarding a CSRF-token.<p>
 * 11. When there is any Content-Security-Policy-violation.<p>
 * // Todo erweitern
 *
 */
public final class NotificationController {

    private NotificationController() {}

    /**
     * The logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    /**
     * The token see: https://core.telegram.org/bots in order how to create a bot.
     * Also see the mentioned references in this document in order to get further information.
     * Like how to use the API: https://core.telegram.org/bots/api
     */
    private static final String TOKEN_ID = "000000000:00000000000000000000000000000000000";

    /**
     * For the chat-ID see the same documents as already mentioned.
     * In order to obtain the ID, the tool cURL (see: http://curl.haxx.se/) could be used in order to send requests.
     * Especially the API-method getUpdates could be used in order to obtain the chat-ID.
     */
    private static final String CHAT_ID = "0000000";

    /**
     * This method uses the mobile instant messenger Telegram, see: https://telegram.org/
     * A notification will be sent to a bot which can communicate to a responsible person or a group of responsible
     * persons.
     *
     * @param message the message to send to the mobile instant messenger.
     */
    public static void sendMessageToTelegram(final String message) {

        Response response = null;
        try {
            response = new OkHttpClient().newCall(new Request.Builder()
                    .url("https://api.telegram.org/bot" + TOKEN_ID + "/sendMessage")
                    .post(new FormBody.Builder()
                            .add("chat_id", CHAT_ID)
                            .add("text", message)
                            .build())
                    .build()).execute();
        } catch (IOException x) {
            LOGGER.error("IOException", "Unerwarteter Fehler aufgetreten.");
        }

        if (response != null && response.isSuccessful()) {
            response.close();
        } else {
            LOGGER.error("Telegram Notification", "A Telegram notification wasn't successful.");
        }
    }
}
