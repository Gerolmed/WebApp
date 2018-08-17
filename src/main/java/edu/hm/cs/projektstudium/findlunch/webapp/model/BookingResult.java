package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * Descripbes requirements for bookings.
 */
@ApiModel(
		description = "Beschreibt Forderungen zu Buchungen."
)
@Getter
@Setter
public class BookingResult {

	/** The sum of Claims */
	@ApiModelProperty(notes = "Forderungssumme")
	private float allClaim;
	
	/** The paid Claim */
	@ApiModelProperty(notes = "Bezahlte Forderung")
	private float paidClaim;
	
	/** The not paid Claim */
	@ApiModelProperty(notes = "Offene Forderung")
	private float notPaidClaim;
	
	/** The start date */
	@ApiModelProperty(notes = "Beginn")
	@DateTimeFormat(pattern="dd.MM.yyy HH:mm")
	//@NotNull(message="{offer.startDate.notNull}")
	private Date startDate;
	
	/** The end date */
	@ApiModelProperty(notes = "Ende")
	@DateTimeFormat(pattern="dd.MM.yyy HH:mm")
	//@NotNull(message="{offer.endDate.notNull}")
	private Date endDate;
}
