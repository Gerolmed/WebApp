package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for weekdays.
 */
@Entity
@Table(name="day_of_week")
@ApiModel(
		description = "Klasse für Wochentag."
)
@Getter
@Setter
@EqualsAndHashCode(of={"ID", "dayNumber", "name"})
public class DayOfWeek {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView(RestaurantView.RestaurantRest.class)
	private int id;

	/** The day number. */
	@ApiModelProperty(notes = "Wochentag-Nummer")
	@Column(name="day_number")
	@JsonView(RestaurantView.RestaurantRest.class)
	private int dayNumber;

	/** The name. */
	@ApiModelProperty(notes = "Name des Wochentags")
	@JsonView(RestaurantView.RestaurantRest.class)
	private String name;

	/** The offers. */
	@ApiModelProperty(notes = "Angebote am Wochentag")
	//bi-directional many-to-many association to Offer
	@ManyToMany(mappedBy="dayOfWeeks")
	private List<Offer> offers;

	/** The time schedules. */
	@ApiModelProperty(notes = "Zeitpläne")
	//bi-directional many-to-one association to TimeSchedule
	@OneToMany(mappedBy="dayOfWeek")
	private List<TimeSchedule> timeSchedules;

	/**
	 * Instantiates a new day of week.
	 */
	public DayOfWeek() {
	}

	// TODO: Schauen, was das genau tut und ob es noch nützlich ist.
	@PreRemove
	private void removeDayOfWeeksFromOffer() {
		for (Offer offer : offers) {
			offer.getDayOfWeeks().remove(offer);
		}
	}

	/**
	 * Adds the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule addTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().add(timeSchedule);
		timeSchedule.setDayOfWeek(this);

		return timeSchedule;
	}

	/**
	 * Removes the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule removeTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().remove(timeSchedule);
		timeSchedule.setDayOfWeek(null);

		return timeSchedule;
	}
}