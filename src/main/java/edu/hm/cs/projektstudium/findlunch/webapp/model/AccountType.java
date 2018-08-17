package edu.hm.cs.projektstudium.findlunch.webapp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Defines different types of accounts.
 */
@Entity
@ApiModel(
		description = "Definiert unterschiedliche Accounttypen."
)
@Getter
@Setter
public class AccountType {

	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@ApiModelProperty(notes = "ID")
	private int id;
	
	/** The name. */
	@ApiModelProperty(notes = "Name")
	private String name;
	
	/** The accounts. */
	@ApiModelProperty(notes = "Accounts")
	@OneToMany(mappedBy="accountType")
	private List<Account> accounts;

}
