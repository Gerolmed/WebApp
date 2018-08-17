package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.PointsView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * Defines points of a restaurant and user.
 */
@Embeddable
@ApiModel(
		description = "Definiert zu einem Restaurant und Kunden Punkte."
)
@Getter
@Setter
public class PointId implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The user.*/
	@ApiModelProperty(notes = "Benutzer")
	@ManyToOne(cascade=CascadeType.ALL)
	private User user;
	
	/** The restaurant.*/
	@ApiModelProperty(notes = "Restaurant")
	@ManyToOne(cascade=CascadeType.ALL)
	@JsonView(PointsView.PointsRest.class)
	private Restaurant restaurant;
	
	public PointId() { super(); }
}