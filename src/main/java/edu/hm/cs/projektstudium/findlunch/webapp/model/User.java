package edu.hm.cs.projektstudium.findlunch.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A user of the FindLunch system. Sets information about a user.
 */
@Entity
// Inherited property needs to be ignored or else the RegisterUserRestController integration test is not working when passing a user object to the request.
@JsonIgnoreProperties({"authorities"})
@ApiModel(
		description = "Ein Benutzer des FindLunch-Systems."
)
@Getter
@Setter
public class User implements UserDetails {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The password. */
	@ApiModelProperty(notes = "Passwort")
	@NotBlank(message="{user.passwordEmpty}")
	private String password;
	
	/** The passwordconfirm. */
	@ApiModelProperty(notes = "Passwort-Bestätigung")
	@Transient
	private String passwordconfirm;
	
	/** The username. */
	@ApiModelProperty(notes = "Benutzername")
	@NotBlank(message="{user.usernameEmpty}")
	private String username;

	/**
	 * A user object has a Captcha object.
	 */
	@ApiModelProperty(notes = "Captcha-Objekt des Benutzers")
	@Transient
	private Captcha captcha;

	/** The favorites. */
	//bi-directional many-to-many association to Restaurant
	@ApiModelProperty(notes = "Favoriten")
	@ManyToMany
	@JoinTable(
		name="favorites"
		, joinColumns={
			@JoinColumn(name="user_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="restaurant_id")
			}
		)
	private List<Restaurant> favorites;

	/** The restaurant. */
	@ApiModelProperty(notes = "Restaurants")
	//bi-directional many-to-one association to Restaurant
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Restaurant restaurant;

	/** The user type. */
	@ApiModelProperty(notes = "Benutzergruppe")
	//bi-directional many-to-one association to UserType
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_type_id")
	private UserType userType;
	
	/** The reservations.*/
	@ApiModelProperty(notes = "Reservierungen")
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	private List<Reservation> reservation; 
	
	/** The points of the user.*/
	@ApiModelProperty(notes = "Punkte")
	@OneToMany(mappedBy="compositeKey.user", cascade=CascadeType.ALL)
	private List<Points> userPoints;
	
	/** The account.*/
	@ApiModelProperty(notes = "Account")
	@ManyToOne
	private Account account;

	@ApiModelProperty(notes = "Passwortzurücksetzung")
	@OneToOne(mappedBy="user")
	private ResetPassword resetPassword;

	@ApiModelProperty(notes = "Absender")
	@Transient
	private SseEmitter emitter;

	@ApiModelProperty(notes = "Push-Benachrichtigung eingeschaltet")
	private boolean pushNotificationEnabled;

	/** The user version. */
	@ApiModelProperty(notes = "Version")
	//bi-directional many-to-one association to Version
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="version_id")
	private Version version;

	/** The last login. */
	@ApiModelProperty(notes = "Last Login data")
	@Column(name="last_login")
	@NotNull
	private Timestamp lastLogin;

	/** The first login. */
	@ApiModelProperty(notes = "First Login data")
	@Column(name="first_login")
	@NotNull
	private Timestamp firstLogin;

	public SseEmitter getEmitter() {
		return emitter;
	}

	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}

	/**
	 * Instantiates a new user.
	 */
	public User() {
		super();

		//Set dates to current time. Will be overwritten if already existent
		lastLogin = new Timestamp(System.currentTimeMillis());
		firstLogin = new Timestamp(System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(getUserType().getName()));
		return authorities;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	/**
	 * Makes sure the account is not expired.
	 * @return true
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
	 */
	/**
	 * Makes sure the account is not locked.
	 * @return true
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	/**
	 * Makes sure the credentials are not expired.
	 * @return true
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	/**
	 * Makes sure the account is enabled.
	 * @return true
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}