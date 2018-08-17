package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the bonus point system.
 */
@Entity
@AssociationOverrides({
	@AssociationOverride(name="compositeKey.user", joinColumns=@JoinColumn(name="user_id")),
	@AssociationOverride(name="compositeKey.restaurant", joinColumns=@JoinColumn(name="restaurant_id"))})
@ApiModel(
		description = "Bonuspunkte"
)
@Getter
@Setter
public class Points {

	/**
	 * The point id (composite key).
	 */
	@ApiModelProperty(notes = "ID")
	//composite-id key
	@EmbeddedId
	@JsonView(PointsView.PointsRest.class)
	private PointId compositeKey;
	
	/** The points.*/
	@ApiModelProperty(notes = "Punkte")
	@JsonView(PointsView.PointsRest.class)
	private int points;
	
	/**
	 * Default Constructor.
	 */
	public Points() { super(); }
	
	/**
	 * Gets the User.
	 * @return The user
	 */
	@Transient
	public User getUser(){
		return compositeKey.getUser();
	}
	
	/**
	 * Sets the new User to Points.
	 * @param user The user
	 */
	public void setUser(User user){
		compositeKey.setUser(user);
	}
	
	/**
	 * Gets the restaurant from points.
	 * @return The restaurant
	 */
	@Transient
	public Restaurant getRestaurant(){
		return compositeKey.getRestaurant();
	}
	
	/**
	 * Sets the new restaurant.
	 * @param restaurant The restaurant
	 */
	public void setRestaurant(Restaurant restaurant){
		compositeKey.setRestaurant(restaurant);
	}

}
