package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;


/**
 * Describes the opening time of a restaurant.
 */
@Entity
@Table(name="opening_time")
@ApiModel(
		description = "Beschreibt die Öffnungszeiten eines Restaurants."
)
@Getter
@Setter
public class OpeningTime {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The closing time. */
	@ApiModelProperty(notes = "Schließungszeit")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", locale="de", timezone="Europe/Berlin")
	@DateTimeFormat(pattern="HH:mm")
	@Column(name="closing_time")
	private Date closingTime;

	/** The opening time. */
	@ApiModelProperty(notes = "Öffnungszeit")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", locale="de", timezone="Europe/Berlin")
	@DateTimeFormat(pattern="HH:mm")
	@Column(name="opening_time")
	private Date openingTime;

	/** The time schedule. */
	@ApiModelProperty(notes = "Geschäftszeiten")
	//bi-directional many-to-one association to TimeSchedule
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="time_schedule_id")
	private TimeSchedule timeSchedule;

	/**
	 * Instantiates a new opening time.
	 */
	public OpeningTime() { super(); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		OpeningTime other = (OpeningTime) obj;
		if (Integer.valueOf(id) == null) {
			if (Integer.valueOf(other.id) != null)
				return false;
		}

		if (id != other.getId()) {
			return false;
		}

		if(openingTime != null && other.getOpeningTime() != null) {
			if(!openingTime.equals(other.getOpeningTime())) {
				return false;
			}
		}

		if (closingTime != null && other.getClosingTime() != null) {
			if (!closingTime.equals(other.getClosingTime())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Integer.valueOf(id) == null) ? 0 : (Integer.valueOf(id).hashCode()));

		return result;
	}
}