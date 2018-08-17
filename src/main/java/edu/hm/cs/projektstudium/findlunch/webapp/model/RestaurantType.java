package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Defines the type of restaurant.
 */
@Entity
@Table(name="restaurant_type")
@ApiModel(
		description = "Definiert Restauranttypen."
)
@Getter
@Setter
public class RestaurantType  {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(RestaurantView.RestaurantRest.class)
	private int id;

	/** The name. */
	@ApiModelProperty(notes = "Name")
	@JsonView(RestaurantView.RestaurantRest.class)
	private String name;

	/** The restaurants. */
	@ApiModelProperty(notes = "Restaurants")
	//bi-directional many-to-one association to Restaurant
	@OneToMany(mappedBy="restaurantType", fetch = FetchType.LAZY)
	private List<Restaurant> restaurants;

	/**
	 * Instantiates a new restaurant type.
	 */
	public RestaurantType() { super(); }

	/**
	 * Adds the restaurant.
	 *
	 * @param restaurant the restaurant
	 * @return the restaurant
	 */
	public Restaurant addRestaurant(Restaurant restaurant) {
		getRestaurants().add(restaurant);
		restaurant.setRestaurantType(this);

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
		restaurant.setRestaurantType(null);

		return restaurant;
	}

}

