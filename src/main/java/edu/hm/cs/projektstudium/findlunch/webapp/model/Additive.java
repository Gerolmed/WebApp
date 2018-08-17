package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Describes the additives of a product.
 */
@Entity
@Table(name="additives")
@ApiModel(
		description = "Beschreibt einen Zusatz, der in Produkten enthalten sein kann."
)
@Getter
@Setter
public class Additive {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty(notes = "ID")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private int id;

	/** The name. */
	@Column(name="name")
	@ApiModelProperty(notes = "Name")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private String name;

	/** The description. */
	@Column(name="description")
	@ApiModelProperty(notes = "Beschreibung")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private String description;

	/** The short Key. */
	@Column(name="short")
	@ApiModelProperty(notes = "Kurzbezeichnung")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private String shortKey;

	/** The offers. */
	@ApiModelProperty(notes = "Die zugehörigen Angebote")
	//bi-directional many-to-many association to Offer
	@ManyToMany(mappedBy="additives")
	private List<Offer> offers;
	
	/**
	 * Instantiates a new additive.
	 */
	public Additive() {
	}
}