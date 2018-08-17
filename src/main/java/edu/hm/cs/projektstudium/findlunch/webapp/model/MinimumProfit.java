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
 * Defines the minimal profit for a restaurant.
 */
@Entity
@ApiModel(
		description = "Definiert den minimalen Gewinn für ein Restaurant."
)
@Getter
@Setter
public class MinimumProfit {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The profit.*/
	@ApiModelProperty(notes = "Gewinn")
	private float profit;
	
	/** The bills.*/
	@ApiModelProperty(notes = "Minimaler Gewinn")
	@OneToMany(mappedBy="minimumProfit")
	private List<Bill> bills;

	public MinimumProfit() { super(); }
}
