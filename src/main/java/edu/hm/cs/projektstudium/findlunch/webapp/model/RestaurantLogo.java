package edu.hm.cs.projektstudium.findlunch.webapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Sets the logo of a restaurant.
 */
@Entity
@Table(name="restaurant_logo")
@ApiModel(
		description = "Logo zu einem Restaurant."
)
@Getter
@Setter
public class RestaurantLogo {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@JsonView(RestaurantView.RestaurantRest.class)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The logo. */
	@ApiModelProperty(notes = "Logo")
	@Lob
	@JsonView(RestaurantView.RestaurantRest.class)
	private byte[] logo;
	
	/** The offer. */
	@ApiModelProperty(notes = "Angebot")
	//bi-directional many-to-one association to Offer
	@ManyToOne(fetch=FetchType.LAZY)
	private Restaurant restaurant;
	
	/** The thumbnail. */
	@ApiModelProperty(notes = "Thumbnail")
	@Lob
	@JsonView(RestaurantView.RestaurantRest.class)
	private byte[] thumbnail;
	
	/** The base 64 encoded. */
	@ApiModelProperty(notes = "Logo base64-kodiert")
	@Transient
	@JsonIgnore
	private String base64Encoded;
	
	/** The image format. */
	@ApiModelProperty(notes = "Bildformat")
	@Transient
	@JsonIgnore
	private String imageFormat;
	
	/**
	 * Instantiates a new Restaurant Logo.
	 */
	public RestaurantLogo() { super(); }

}
