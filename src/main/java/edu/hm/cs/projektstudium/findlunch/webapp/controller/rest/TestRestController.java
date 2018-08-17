package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.service.FCMPushService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


/**
 * Wird benutzt, um manuell per REST-Call das Abholen der täglichen Angebote in der Nähe anzustoßen.
 */
@RestController
public class TestRestController {

    /** The logger. */
    private final Logger LOGGER = LoggerFactory.getLogger(TestRestController.class);

    /** Push-Service zum Versenden der Nachrichten */
    private final FCMPushService fcmPushService;

    /**
     * Konstruktor
     * @param fcmPushService Die Service-Klasse für Pushes
     */
    @Autowired
    public TestRestController(FCMPushService fcmPushService) {
        this.fcmPushService = fcmPushService;
    }

    /**
     *
     * @param pushToken Der FCM-Push-Token des Geräts, an das gesendet werden soll
     * @param request   Der Http-Request-Body (leer).
     * @return          Status zurückgeben.
     */
    @CrossOrigin
    @RequestMapping(
            path = "/api/testPush",
            method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Push-Notifikation erfolgreich deaktiviert.")
            })
    public ResponseEntity<String> testPush(
            @RequestParam String pushToken,
            HttpServletRequest request) {
        LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));



        fcmPushService.sendTestNotification(pushToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(
            path = "/api/testContext",
            method = RequestMethod.GET)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Push-Notifikation erfolgreich deaktiviert.")
    })
    public ResponseEntity<String> testContext(
            HttpServletRequest request) {
        LOGGER.info(LogUtils.getInfoStringWithParameterList(request, Thread.currentThread().getStackTrace()[1].getMethodName()));


        return new ResponseEntity<>("ContextPath: " + request.getContextPath() +
                " Path: " + request.getPathInfo(), HttpStatus.OK);
    }

}
