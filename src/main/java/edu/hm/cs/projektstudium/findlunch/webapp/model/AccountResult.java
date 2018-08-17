package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Describes the yield of a account.
 */
@ApiModel(
		description = "Beschreibt das Ergebnis zum Account."
)
@Getter
@Setter
public class AccountResult {
	
	/** The account Number.*/
	@ApiModelProperty(notes = "Die Accountnummer")
	private int accountNumber;
	
	/** The sum of Amount.*/
	@ApiModelProperty(notes = "Betragssumme")
	private float sumOfAmount;
	
	/** The customer id .*/
	@ApiModelProperty(notes = "Kundennummer")
	private int customerId;

}
