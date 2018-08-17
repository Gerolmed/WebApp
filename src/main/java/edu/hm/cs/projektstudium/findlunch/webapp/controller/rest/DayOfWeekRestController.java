package edu.hm.cs.projektstudium.findlunch.webapp.controller.rest;

import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.logging.LogUtils;
import edu.hm.cs.projektstudium.findlunch.webapp.model.DayOfWeek;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.DayOfWeekRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
/**
 * The class is responsible for handling rest calls related to DayOfWeeks.
 * Rest controllers mapping api.
 */
@RestController
@Api(
        value="Wochentage",
        description="Behandlung von Wochentags-Anfragen.")
public class DayOfWeekRestController {

	/** The DayOfWeekRepository repository. */
	private final DayOfWeekRepository dowRepository;

	/** The logger. */
	private final Logger LOGGER = LoggerFactory.getLogger(DayOfWeekRestController.class);

    @Autowired
    public DayOfWeekRestController(DayOfWeekRepository dowRepository) {
        this.dowRepository = dowRepository;
    }

    /**
	 * Gets all days of week.
	 *
	 * @param request the HttpServletRequest
	 * @return all days of week
	 */
	@CrossOrigin
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiOperation(
	        value = "Abrufen der Wochentage.",
            response = List.class)
	@RequestMapping(
	        path = "/api/days_of_week",
            method = RequestMethod.GET,
            produces = "application/json")
	public List<DayOfWeek> getAllDaysOfWeek(HttpServletRequest request) {
		LOGGER.info(LogUtils.getDefaultInfoString(request, Thread.currentThread().getStackTrace()[1].getMethodName()));
		return dowRepository.findAll();
	}
}
