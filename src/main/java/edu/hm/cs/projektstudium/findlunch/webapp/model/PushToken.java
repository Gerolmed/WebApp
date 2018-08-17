package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The class represents a firebase push token.
 */
@Entity
@Table(name="user_pushtoken")
@ApiModel(
		description = "Beschreibt einen Token für die Push-Mitteilungen."
)
@Getter
@Setter
public class PushToken {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The user*/
	@ApiModelProperty(notes = "Benutzer")
	@Column(name="user_id")
	@NotNull
	private int userId;
	
	/** The token*/
	@ApiModelProperty(notes = "FCM-Token")
	@Column(name="fcm_token")
	@NotNull
	private String fcmToken;
	
	public PushToken() { super(); }
	
}
