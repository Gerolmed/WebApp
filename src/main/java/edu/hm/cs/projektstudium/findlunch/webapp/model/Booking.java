package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * Describes book entrys.
 */
@Entity
@ApiModel(
		description = "Buchungssatz"
)
@Getter
@Setter
public class Booking {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty(notes = "ID")
	private int id;
	
	/** The book id. */
	@ApiModelProperty(notes = "Buchungsnummer")
	private int bookId;
	
	/** The amount. */
	@ApiModelProperty(notes = "Betrag")
	@NumberFormat(style=Style.DEFAULT)
	private float amount;
	
	/** The booking time */
	@ApiModelProperty(notes = "Buchungszeitpunkt")
	private Date bookingTime;

	@ApiModelProperty(notes = "Buchungsgrund")
	@ManyToOne(fetch = FetchType.EAGER)
	private BookingReason bookingReason;
	
	/** The account. */
	@ApiModelProperty(notes = "Account")
	@ManyToOne(fetch = FetchType.EAGER)
	private Account account;
	
	/** The bill. */
	@ApiModelProperty(notes = "Rechnung")
	@ManyToOne(fetch = FetchType.EAGER)
	private Bill bill;
}
