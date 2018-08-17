package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class makes it easier for administrators to get log files.
 */
@RestController
@Api(
        value = "Logfiles",
        description = "Verwaltung von Log-Dateien.")
public class LogRestController {

    /**
     * The logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(LogRestController.class);

    /**
     * Gets the requested logfile.
     *
     * @param request the HttpServletRequest
     * @param file Name of the log file
     * @return the log file
     * @throws IOException if the underlying service fails.
     */
    @CrossOrigin
    @ApiOperation(
            value = "Abrufen der Log-Datei.",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Log-Datei erfolgreich abgerufen.")
    })
    @RequestMapping(
            path = "/api/logs",
            method = RequestMethod.GET,
            produces = "text/html")
    public final String getLogfile(
            @RequestParam @ApiParam(
                    value = "Log-Datei",
                    required = true)
            File file,
            final HttpServletRequest request) throws IOException {
        LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));

        String logfile = "";
        final FileReader fileReader = new FileReader(file);
        int position = fileReader.read();
        while (position != -1) {
            logfile += (char) position;
            position = fileReader.read();
        }
        fileReader.close();
        return logfile
                + "\n"
                + file.getAbsolutePath();

    }

}
