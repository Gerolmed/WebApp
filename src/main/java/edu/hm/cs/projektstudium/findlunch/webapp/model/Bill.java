package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import javax.persistence.*;


/**
 * Describes the bill of a purchase.
 */
@Entity
@ApiModel(
		description = "Beschreibt eine Rechnung zu einem Kauf."
)
@Getter
@Setter
public class Bill {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty(notes = "ID")
	private int id;
	
	/** The bill number. */
	@ApiModelProperty(notes = "Rechnungsnummer")
	private String billNumber;
	
	/** The start date. */
	@ApiModelProperty(notes = "Beginn")
	private Date startDate;
	
	/** The end date. */
	@ApiModelProperty(notes = "Ende")
	private Date endDate;
	
	/** The total price. */
	@ApiModelProperty(notes = "Gesamtpreis")
	private float totalPrice;
	
	/** Is paid. */
	@ApiModelProperty(notes = "Bezahlt?")
	private boolean paid;
	
	/**
	 * The minimumProfit.
	 */
	@ApiModelProperty(notes = "Geringster Gewinn")
	@ManyToOne(fetch = FetchType.EAGER)
	private MinimumProfit minimumProfit;
	
	/**
	 * List of reservation for this bill.
	 */
	@ApiModelProperty(notes = "Reservierungsliste für diese Rechnung")
	@OneToMany(mappedBy="bill")
	private List<Reservation> reservations;
	
	/**
	 * The restaurant.
	 */
	@ApiModelProperty(notes = "Restaurant")
	@ManyToOne(fetch = FetchType.EAGER)
	private Restaurant restaurant;
	
	/**
	 * The donationPerMonth.
	 */
	@ApiModelProperty(notes = "Monatliche Spenden")
	@OneToMany(mappedBy= "bill")
	private List<DonationPerMonth> ListOfDonationPerMonth;
	
	/**
	 * The pdf in bytes.
	 */
	@ApiModelProperty(notes = "PDF")
	@Lob
	private byte[] billPdf;
	
	/**
	 * The bookings of the bill.
	 */
	@ApiModelProperty(notes = "Buchungen")
	@OneToMany(mappedBy="bill")
	private List<Booking> bookings;
}
