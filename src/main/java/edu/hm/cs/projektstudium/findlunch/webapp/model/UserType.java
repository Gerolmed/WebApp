package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * Defines different types of users.
 */
@Entity
@Table(name="user_type")
@ApiModel(
		description = "Definiert verschiedene Benutzertypen."
)
@Getter
@Setter
public class UserType {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	private int id;

	/** The name. */
	@ApiModelProperty(notes = "Name")
	private String name;

	/** The users. */
	//bi-directional many-to-one association to User
	@ApiModelProperty(notes = "Benutzer")
	@OneToMany(mappedBy="userType")
	private List<User> users;

	/**
	 * Instantiates a new user type.
	 */
	public UserType() { super(); }

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return the user
	 */
	public User addUser(User user) {
		getUsers().add(user);
		user.setUserType(this);

		return user;
	}

	/**
	 * Removes the user.
	 *
	 * @param user the user
	 * @return the user
	 */
	public User removeUser(User user) {
		getUsers().remove(user);
		user.setUserType(null);

		return user;
	}

}