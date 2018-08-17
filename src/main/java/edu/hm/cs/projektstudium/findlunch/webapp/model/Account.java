package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The class Account.
 */
@Entity
@ApiModel(
		description = "Ein Account."
)
@Getter
@Setter
public class Account {

	/** The id */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The account number.*/
	@ApiModelProperty(notes = "Die Accountnummer")
	private int accountNumber;
	
	/** The account type.*/
	@ApiModelProperty(notes = "Der Accounttyp")
	@ManyToOne(fetch = FetchType.EAGER)
	private AccountType accountType;
	
	/** The bookings from this account.*/
	@ApiModelProperty(notes = "Die Buchungen von diesem Account")
	@OneToMany(mappedBy="account")
	private List<Booking> bookings;
	
	/** The admins of this account.*/
	@ApiModelProperty(notes = "Die Administratoren des Accounts")
	@OneToMany(mappedBy = "account")
	private List<User> users;
	
	/**
	 * Instantiates a new account.
	 */
	public Account(){
		this.users = new ArrayList<User>();
	}
	
	/**
	 * Add a user as Admin
	 * @param user The user
	 * @return Added user
	 */
	public User addUser(User user){
		getUsers().add(user);
		user.setAccount(this);
		
		return user;
	}
}
