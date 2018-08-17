package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Class to define the reservation of a product.
 */
@Entity
@ApiModel(
		description = "Definiert Reservierung eines Produkts."
)
@Getter
@Setter
public class Reservation {
	
	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@JsonView({ReservationView.ReservationRest.class})
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The donation. */
	@ApiModelProperty(notes = "Spende")
	@JsonView({ReservationView.ReservationRest.class})
	private float donation;

	/** The total price. */
	@ApiModelProperty(notes = "Gesamtpreis")
	@JsonView({ReservationView.ReservationRest.class})
	private float totalPrice;

	/** The fee to be payed to PayPal **/
	@JsonView({ReservationView.ReservationRest.class})
	private float fee;

	/** Is used points. */
	@ApiModelProperty(notes = "Benutzte Punkte")
	@JsonView({ReservationView.ReservationRest.class})
	private boolean usedPoints;

	/** Used PayPal for payment */
	@Column(name="used_paypal")
	@JsonView({ReservationView.ReservationRest.class})
	private boolean usedPaypal;

	/** Braintree Transaction ID. Uniquely identifies the transaction, is needed to settle or void it
	 * 	More information: https://developers.braintreepayments.com/reference/general/statuses
	 */
	@Column(name="pp_transaction_id")
	@JsonView({ReservationView.ReservationRest.class})
	private String ppTransactionId;

	/**
	 * Makes sure the PayPal-Transaction has succesfully been fninished (either voided or settled)
	 */
	@Column(name="pp_transaction_finished")
	@JsonView({ReservationView.ReservationRest.class})
	private boolean ppTransactionFinished;
	
	/** Points are Collected by the cusomer */
	@Column(name="points_collected")
	@JsonView({ReservationView.ReservationRest.class})
	private boolean pointsCollected;
	
	/** The points collected by the customer */
	@ApiModelProperty(notes = "Gesammelte Punkte")
	@Column(name="points")
	@JsonView({ReservationView.ReservationRest.class})
	private float points;
	
	/** The reservation number*/
	@ApiModelProperty(notes = "Bestellnummer")
	@JsonView({ReservationView.ReservationRest.class})
	private int reservationNumber;

	/** The user.*/
	@ApiModelProperty(notes = "Benutzer")
	// @JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;
	
	/** The offers within the reservation */
	@ApiModelProperty(notes = "Angebote in der Bestellung")
	@OneToMany(mappedBy="reservation", cascade=CascadeType.ALL)
	@JsonView({ReservationView.ReservationRest.class})
	private List<ReservationOffers> reservation_offers;
	
	/** The euroPerPoint.*/
	@ApiModelProperty(notes = "Euro pro Punkt")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="euro_per_point_id")
	private EuroPerPoint euroPerPoint;
	
	/** The restaurant.*/
	@ApiModelProperty(notes = "Restaurant")
	@JsonView({ReservationView.ReservationRest.class})
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurant_id")
	private Restaurant restaurant;
	
	/** The bill.*/
	@ApiModelProperty(notes = "Rechnung")
	@ManyToOne(fetch=FetchType.EAGER)
	//@JsonView({ReservationView.ReservationRest.class})
	private Bill bill;

	/** The reservationStatus.*/
	@ApiModelProperty(notes = "Reservierungsstatus")
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonView({ReservationView.ReservationRest.class})
	@JoinColumn(name="reservation_status_id")
	private ReservationStatus reservationStatus;
	
	/** The collect_time. */
	@ApiModelProperty(notes = "Zeit")
	@JsonView({ReservationView.ReservationRest.class})
	@Column(name="collect_time")
	private Date collectTime;
	
	/** The reservation time. */
	@ApiModelProperty(notes = "Bestellzeit empfangen")
	//@JsonView({ReservationView.ReservationRest.class})
	@Column(name="timestamp_received")
	private Date timestampReceived;
	
	/** The reservation time. */
	@ApiModelProperty(notes = "Geantwortete Bestellzeit")
	//@JsonView({ReservationView.ReservationRest.class})
	@Column(name="timestamp_responded")
	private Date timestampResponded;

	/**
	 * 	Payment nonce, used when payment is done via Paypal. This is only in here to be mapped in an incoming REST call.
	 * 	As this is one-time use sensitive information it will not be stored in the database and is therefore @Transient.
	 */
	@Transient
	@JsonView({ReservationView.ReservationRest.class})
	private String nonce;

	public Reservation() { super(); }

	/**
	 * @return boolean true when Reservation is confirmed
	 */
	public boolean isConfirmed(){
		return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_CONFIRMED;
	}
	
	/**
	 * @return boolean true when Reservation is Rejected
	 */
	public boolean isRejected(){
		return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_REJECTED;
	}
	
	/**
	 * @return boolean true when Reservation is Unprocessed
	 */
	public boolean isUnprocessed(){
	    return reservationStatus.getKey() == ReservationStatus.RESERVATION_KEY_UNPROCESSED;
	}
}
