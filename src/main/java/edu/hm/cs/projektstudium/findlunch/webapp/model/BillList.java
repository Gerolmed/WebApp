package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Combines multiple bills to a list of bills.
 */
@ApiModel(
		description = "Fasst mehrere Rechnungen zu einer Liste zusammen."
)
@Getter
@Setter
public class BillList {
	
	/** The list of bills */
	@ApiModelProperty(notes = "Rechnungsliste")
	private ArrayList<Bill> bills = new ArrayList<Bill>();

}
