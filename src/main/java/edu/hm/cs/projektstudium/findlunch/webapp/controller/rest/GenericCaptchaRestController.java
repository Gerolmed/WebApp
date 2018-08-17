package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import io.swagger.annotations.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class could be used for a generic Captcha handling process.
 * For example Captchas or responses for Captchas from different providers could be obtained through the methods of
 * this class.
 */
@RestController
@Api(
        value="Captcha-Behalung",
        description="Kann für Captcha-Handling-Prozess verwendet werden.")
public class GenericCaptchaRestController {
    private final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GenericCaptchaRestController.class);

    /**
     * Gets the Captcha or a Captcha-response of the requested captcha provider.
     *
     * @param request the HttpServletRequest
     * @param provider the captcha provider
     * @return the log file.
     * @throws IOException if the underlying service fails.
     */
    @CrossOrigin
    @ApiOperation(
            value = "Abruf eines Captcha vom gewünschten Provider.",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Captcha erfolgreich abgerufen."),
            @ApiResponse(code = 400, message = "Fehler beim Abruf.")
    })
    @RequestMapping(
            path = "/api/captcha",
            method = RequestMethod.GET,
            produces = "text/html")
    public final ResponseEntity<String> getRemoteContent(
            @RequestParam(name = "provider")
            @ApiParam(
                    value = "URL des Captcha-Anbieters",
                    required = true)
            String provider,
            final HttpServletRequest request) throws IOException {
        LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

        Response response = null;
        try {
            response = new OkHttpClient().newCall(new Request.Builder()
                    .url(provider)
                    .get()
                    .build()).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return new ResponseEntity<>(response != null ? response.body().string() : "No body", HttpStatus.OK);
    }
}
