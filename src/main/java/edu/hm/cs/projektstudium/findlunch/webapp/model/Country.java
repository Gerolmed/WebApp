package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * The country of a business.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown=true)
@ApiModel(
		value = "Land",
		description = "Land, in dem Geschäft ist."
)
@Getter
@Setter
public class Country {
	
	/** The country code. */
	@ApiModelProperty(notes = "Ländercode")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="country_code")
	private String countryCode;

	/** The name. */
	@ApiModelProperty(notes = "Name des Landes")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private String name;

	@ApiModelProperty(notes = "Liste der zuständigen Vertriebsmitarbeiter")
	@OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
	private List<SalesPerson> salesPersons;

	/** The restaurants. */
	//bi-directional many-to-one association to Restaurant
	@ApiModelProperty(notes = "Restaurants im Land")
	@JsonIgnore
	@OneToMany(mappedBy="country", fetch=FetchType.LAZY)
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new country.
	 */
	public Country() {
		
		this.restaurants = new ArrayList<Restaurant>();
		
	}

	/**
	 * Adds the restaurant.
	 *
	 * @param restaurant the restaurant
	 * @return the restaurant
	 */
	public Restaurant addRestaurant(Restaurant restaurant) {
		getRestaurants().add(restaurant);
		restaurant.setCountry(this);

		return restaurant;
	}

	/**
	 * Removes the restaurant.
	 *
	 * @param restaurant the restaurant
	 * @return the restaurant
	 */
	public Restaurant removeRestaurant(Restaurant restaurant) {
		getRestaurants().remove(restaurant);
		restaurant.setCountry(null);

		return restaurant;
	}

}