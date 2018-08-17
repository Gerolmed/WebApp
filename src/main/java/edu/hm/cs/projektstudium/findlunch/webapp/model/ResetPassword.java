package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Class to reset the password, includes data for the reset.
 */
@Entity
@ApiModel(
		description = "Beinhaltet Daten zum Zurücksetzen des Passwortes."
)
@Getter
@Setter
public class ResetPassword {
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	/**
	 * The ID.
	 */
	private int id;

	/**
	 * The token.
	 */
	@ApiModelProperty(notes = "Token")
	private String token;

	/**
	 * The date.
	 */
	@ApiModelProperty(notes = "Datum")
	private Date date;

	/**
	 * The user.
	 */
	@ApiModelProperty(notes = "Benutzer")
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User user;

    public ResetPassword() { super(); }
}
