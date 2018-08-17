package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Describes the reason for a booking.
 */
@Entity
@ApiModel(
		description = "Beschreibt den Grund für eine Buchung."
)
@Getter
@Setter
public class BookingReason {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The reason. */
	@ApiModelProperty(notes = "Grund")
	private String reason;
	
	/** The bookings. */
	@ApiModelProperty(notes = "Buchungen")
	@OneToMany(mappedBy="bookingReason")
	private List<Booking> bookings;
}
