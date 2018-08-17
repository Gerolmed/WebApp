package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Information about the state of a reservation.
 */
@Entity
@Table(name="reservation_status")
@ApiModel(
		description = "Status der Reservierung."
)
@Getter
@Setter
public class ReservationStatus {
	
	public static final int RESERVATION_KEY_NEW = 0;
	public static final int RESERVATION_KEY_CONFIRMED = 1;
	public static final int RESERVATION_KEY_REJECTED = 2;
	public static final int RESERVATION_KEY_UNPROCESSED = 3;
	
	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@JsonView({ReservationView.ReservationRest.class})
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	/** The status. */
	@ApiModelProperty(notes = "Bestellstatus")
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="status")
	private String status;
	
	/** The key. */
	@ApiModelProperty(notes = "Schlüssel")
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="statkey")
	private int key;

	/** The reservations.*/
	@ApiModelProperty(notes = "Bestellungen")
	@OneToMany(mappedBy="reservationStatus")
	private List<Reservation> reservation;

	public ReservationStatus() { super(); }
}
