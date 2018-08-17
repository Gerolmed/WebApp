package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.NotificationController;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;
import edu.hm.cs.projektstudium.findlunch.webapp.security.AuthenticationHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Timestamp;


/**
 * Die Klasse UserRest Controller
 *
 * Wird von der Customer-App genutzt, um User-bezogene Operationen durchzuführen.
 * Augenblicklich ist die Änderung der Push-Subscription und der Login implementiert.
 */
@RestController
public class UserRestController {

    /** The helper class used for handling login attempts. */
    private final AuthenticationHelper authenticationHelper;


    /** The user repository. */
    private final UserRepository userRepository;

    /** The logger. */
    private final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);


    @Autowired
    public UserRestController(AuthenticationHelper authenticationHelper, UserRepository userRepository) {
        this.authenticationHelper = authenticationHelper;
        this.userRepository = userRepository;
    }

    /**
     * REST-Endpunkt zum Aktivieren der Push-Notifikationen des Users.
     *
     * @param principal     Principal des eingeloggten Users.
     * @param request       HTTP-Request
     * @return              Response mit Statuscode.
     */
    @CrossOrigin
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(
            path = "/api/pushNotifications",
            method = RequestMethod.PUT)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Push-Notifikation erfolgreich aktiviert."),
            @ApiResponse(code = 401, message = "User nicht gefunden")})
    public ResponseEntity<String> activatePushNotification(
            HttpServletRequest request,
            Principal principal) {
        LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
        return changePushNotification(principal, true);
    }

    /**
     * REST-Endpunkt zum Deativieren der Push-Notifikationen des Users.
     *
     * @param principal     Principal des eingeloggten Users.
     * @param request       HTTP-Request
     * @return              Response mit Statuscode.
     */
    @CrossOrigin
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(
            path = "/api/pushNotifications",
            method = RequestMethod.DELETE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Push-Notifikation erfolgreich deaktiviert."),
            @ApiResponse(code = 401, message = "User nicht gefunden")})
    public ResponseEntity<String> deactivatePushNotification(
            HttpServletRequest request,
            Principal principal) {
        LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
        return changePushNotification(principal, false);
    }

    /**
     * Methode zum ändern der Push-Benachrichtigungserlaubnis.
     *
     * @param principal Principal des eingeloggten Users
     * @param enabled aktivieren oder deaktivieren
     * @return ResponseEntity mit Ergebnis der Änderung
     */
    private ResponseEntity<String> changePushNotification(Principal principal, boolean enabled) {
        // User anhand ID finden.
        User user = userRepository.getOne(((User) ((Authentication) principal).getPrincipal()).getId());
        if(user!=null) {
            // Wenn User zur ID vorhanden ist, prüfen, ob ID der des anfragenden Users entspricht.
            if (user.getId()>0){
                user.setPushNotificationEnabled(enabled);
                user.setLastLogin(new Timestamp(System.currentTimeMillis()));
                userRepository.save(user);
                return new ResponseEntity<>("push-notifications enabled: " + user.isPushNotificationEnabled(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Login user.
     *
     * @param request the HttpServletRequest
     * @param principal the user principal for authentication
     * @return the response entity representing a status code
     */
    @CrossOrigin
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(
            value = "Einloggen des Benutzers.",
            response = Integer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Erfolgreich angemeldet."),
            @ApiResponse(code = 401, message = "Nicht autorisiert.")
    })
    @RequestMapping(
            path = "/api/login_user",
            method = RequestMethod.GET,
            produces = "application/json")
    public ResponseEntity<String> loginUser(HttpServletRequest request, Principal principal) {
        final String ipAddress = getClientIP(request);

        // As there is no session-ID with the RESTful-Webservice we just can use the retrieved IP-address.
        if (!authenticationHelper.isBlocked(ipAddress)) {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
            // User mit den Attributen ID, Name und pushNotificationEnabled zurückgeben.
            User user = userRepository.getOne(((User) ((Authentication) principal).getPrincipal()).getId());

            return new ResponseEntity<>("{\"pushNotificationEnabled\":\"" + user.isPushNotificationEnabled() + "\"}", HttpStatus.OK);
        } else {
            LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress);

            NotificationController.sendMessageToTelegram(LogUtils.getDefaultInfoString(request,
                    Thread.currentThread().getStackTrace()[1].getMethodName()) +
                    " IP-address banned: " + ipAddress + " There is a potential brute-force-attack against the" +
                    " RESTful-webservice. The potential attacker used the correct password but is banned." +
                    "Better check if it was an valid attempt or a successful attack.");
            throw new LockedException("The IP-address: " + ipAddress + " was blocked.");
        }
    }

    /**
     * This method gets the client's IP-address and pays attention for the X-Forwarded-For header which could
     * identify a proxy user. See for example: https://tools.ietf.org/html/rfc7239
     * When a potential attacker uses a proxy during a brute-force-attack the XFF-header could reveal his
     * real IP-address.
     *
     * @return the client's IP-address
     */
    private String getClientIP(HttpServletRequest request) {
        final String xffHeader = request.getHeader("X-Forwarded-For");
        if (xffHeader == null) {
            return request.getRemoteAddr();
        }
        return xffHeader.split(",")[0];
    }
}
