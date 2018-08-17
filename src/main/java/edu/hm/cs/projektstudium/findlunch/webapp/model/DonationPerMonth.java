package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**

 * Includes the monthly amount, a user wants to donate.
 */
@Entity
@ApiModel(
		description = "Beinhaltet den monatlichen Spendenbetrag, den ein Benutzer spenden möchte."
)
@Getter
@Setter
public class DonationPerMonth {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	
	/** The date. */
	@ApiModelProperty(notes = "Datum")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	private Date date;
	
	/** The update time with date. */
	@ApiModelProperty(notes = "Aktualisierungszeitpunkt")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd.MM.yyy HH:mm")
	private Date datetimeOfUpdate;
	
	/** The amount. */
	@ApiModelProperty(notes = "Spendensumme")
	@NumberFormat(style=Style.DEFAULT)
	@DecimalMin(value="0.0", message="{donationPerMonth.amount.invalidMinValue}")
	@DecimalMax(value="999.99", message="{donationPerMonth.amount.invalidMaxValue}")
	private float amount;
	
	/** The restaurant. */
	@ApiModelProperty(notes = "Restaurant")
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;
	
	/** The bill. */
	@ApiModelProperty(notes = "Rechnung")
	@ManyToOne(fetch = FetchType.EAGER)
	private Bill bill;

	public DonationPerMonth() { super(); }
}
