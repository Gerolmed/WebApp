package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.RestaurantView;

/**
 * Sets important information about a restaurant.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(
		description = "Ein Restaurant."
)
@Getter
@Setter
public class Restaurant implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class, OfferView.OfferRest.class})
	private int id;

	/** The city. */
	@ApiModelProperty(notes = "Stadt")
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.city.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.city.sizeError}")
	@Pattern(regexp = "[\\p{L} ]*", message="{restaurant.city.patternMismatch}")
	private String city;

	/** The email. */
	@ApiModelProperty(notes = "E-Mail-Adresse")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.email.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.email.sizeError}")
	private String email;

	/** The location latitude. */
	@ApiModelProperty(notes = "Breitengrad")
	@Column(name = "location_latitude")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private float locationLatitude;

	/** The location longitude. */
	@ApiModelProperty(notes = "Längengrad")
	@Column(name = "location_longitude")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private float locationLongitude;

	/** The name. */
	@ApiModelProperty(notes = "Name")
	@JsonView({RestaurantView.RestaurantRest.class, ReservationView.ReservationRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.name.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.name.sizeError}")
	@Pattern(regexp = "[\\p{L}0-9-&´`'\"(). ]*", message="{restaurant.name.patternMismatch}")
	private String name;

	/** The phone. */
	@ApiModelProperty(notes = "Telefonnummer")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.phone.notBlank}")
	@Size(min=3, max=60, message= "{restaurant.phone.sizeError}")
	@Pattern(regexp = "[0-9+/()\\- ]{1,}", message="{restaurant.phone.patternMismatch}")
	private String phone;

	/** The street. */
	@ApiModelProperty(notes = "Straße")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.street.notBlank}")
	@Size(min=2, max=60, message= "{restaurant.street.sizeError}")
	private String street;

	/** The street number. */
	@ApiModelProperty(notes = "Hausnummer")
	@Column(name = "street_number")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.streetNumber.notBlank}")
	@Pattern(regexp = "[1-9]{1}[0-9]{0,3}[a-zA-Z]?(-[1-9]{1}[0-9]{0,3}[a-zA-Z]?)?", message="{restaurant.streetNumber.patternMismatch}")
	@Size(min=1, max=11, message= "{restaurant.streetNumber.sizeError}")
	private String streetNumber;

	/** The url. */
	@ApiModelProperty(notes = "Webseite")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@URL(message = "{restaurant.urlInvalid}")
	@Size(max=60, message= "{restaurant.url.sizeError}")
	private String url;

	/** The zip. */
	@ApiModelProperty(notes = "Postleitzahl")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotBlank(message = "{restaurant.zip.notBlank}")
	@NumberFormat(style = Style.NUMBER, pattern = "#####")
	@Size(min=5, max=5, message = "{restaurant.zip.size}")
	@Pattern(regexp = "^([0]{1}[1-9]{1}|[1-9]{1}[0-9]{1})[0-9]{3}$", message="{restaurant.zip.patternMismatch}")
	private String zip;

	/** The offers. */
	@ApiModelProperty(notes = "Angebote")
	// bi-directional many-to-one association to Offer
	@OneToMany(mappedBy = "restaurant")
	private List<Offer> offers;

	/** The country. */
	@ApiModelProperty(notes = "Land")
	// bi-directional many-to-one association to Country
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "country_code")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotNull(message = "{restaurant.country.notNull}")
	private Country country;

	/** The kitchen types. */
	@ApiModelProperty(notes = "Küchentypen")
	// bi-directional many-to-many association to KitchenType
	@ManyToMany
	@JoinTable(name = "restaurant_has_kitchen_type", joinColumns = {
	@JoinColumn(name = "restaurant_id") }, inverseJoinColumns = { @JoinColumn(name = "kitchen_type_id") })
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	@NotEmpty(message = "{restaurant.kitchenType.notEmpty}")
	private List<KitchenType> kitchenTypes;

	/** The restaurant type. */
	@ApiModelProperty(notes = "Restauranttyp")
	// bi-directional many-to-one association to RestaurantType
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "restaurant_type_id")
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private RestaurantType restaurantType;

	/** The time schedules. */
	@ApiModelProperty(notes = "Öffnungszeiten")
	// bi-directional many-to-one association to TimeSchedule
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private List<TimeSchedule> timeSchedules;

	/** The admins. */
	@ApiModelProperty(notes = "Administratoren")
	// bi-directional many-to-one association to User
	@JsonIgnore
	@OneToMany(mappedBy = "restaurant",fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<User> admins;
	
	
	/** The fav users. */
	@ApiModelProperty(notes = "Von Benutzern als Favorit markiert.")
	@ManyToMany
	@JoinTable(name = "favorites", joinColumns = {
	@JoinColumn(name = "restaurant_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<User> favUsers;
	
	/** The default Logo. */
	@ApiModelProperty(notes = "Logo")
	@Transient
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private RestaurantLogo defaultLogo;
	

	/** The distance to a given position. */
	@ApiModelProperty(notes = "Entfernung")
	@Transient
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private int distance;
	
	/** Is favorite restaurant.*/
	@ApiModelProperty(notes = "Favorit?")
	@Transient
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private boolean isFavorite;

	/** The points of restaurant.*/
	@ApiModelProperty(notes = "Punkte")
	@OneToMany(mappedBy="compositeKey.restaurant", cascade= CascadeType.ALL)
	private List<Points>  restaurantPoints;
	
	/** The actual point.*/
	@ApiModelProperty(notes = "Aktuelle Punkte")
	@Transient
	@JsonView({RestaurantView.RestaurantRest.class, OfferView.OfferRest.class})
	private int actualPoints;
	
	/** The reservations.*/
	@ApiModelProperty(notes = "Bestellungen")
	@OneToMany(mappedBy="restaurant")
	List<Reservation> reservation;
	
	/** The uuid of the restaurant.*/
	@ApiModelProperty(notes = "UUID")
	private String restaurantUuid;

	/** The qr-code in bytes.*/
	@ApiModelProperty(notes = "QR-Code")
	@Lob
	private byte[] qrUuid;
	
	/** The qr-code in base64.*/
	@ApiModelProperty(notes = "Base64-kodierter QR-Code")
	@Transient
	private String base64Encoded;
	
	/** The customer id.*/
	@ApiModelProperty(notes = "Kundennummer")
	private int customerId;
	
	/** The donations of the restaurant.*/
	@ApiModelProperty(notes = "Spenden für das Restaurant")
	@OneToMany(mappedBy="restaurant", fetch = FetchType.EAGER)
	private List<DonationPerMonth> donations;
	
	/** The bills of the restaurant.*/
	@ApiModelProperty(notes = "Rechnungen")
	@OneToMany(mappedBy="restaurant")
	private List<Bill> bills;

	/** The offer photos. */
	@ApiModelProperty(notes = "Angebotsbilder")
	//bi-directional many-to-one association to OfferPhoto
	@OneToMany(mappedBy="restaurant", cascade=CascadeType.ALL, orphanRemoval=true )
	private List<RestaurantLogo> restaurantLogos;
	
	@JsonView(RestaurantView.RestaurantRest.class)
	@ApiModelProperty(notes = "Aktuell geöffnet")
	@Transient
	private boolean currentlyOpen;

	@ApiModelProperty(notes = "Berechtigung zum Bearbeiten")
	@Column(name = "swa_offer_modify_permission")
	private boolean offerModifyPermission;

	@ApiModelProperty(notes = "Für Endkunden nicht sichtbar")
	@Column(name = "swa_blocked")
	private boolean blocked;

	@ApiModelProperty(notes = "Zugeordnete Sales-Person")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "swa_sales_person_id")
	private SalesPerson salesPerson;

    @OneToMany(mappedBy = "id", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CourseType> courseTypeList;

	@Transient
	private List<RestaurantTimeContainer> openingTimes;

	@Transient
	private List<RestaurantTimeContainer> offerTimes;

	@Transient
	private String restaurantTypeAsString;

	@Transient
	private List<String> kitchenTypesAsString;

	//Needed for the Regex Validator needs a String
	//Regex-Sourece: https://stackoverflow.com/questions/3518504/regular-expression-for-matching-latitude-longitude-coordinates
	@Transient
	@Pattern(regexp = "(^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$)|", message = "{restaurant.validation.coordinatesLatitude}")
	private String locationLatitudeAsString;

	//Needed for the Regex Validator needs a String
	//Regex-Sourece: https://stackoverflow.com/questions/3518504/regular-expression-for-matching-latitude-longitude-coordinates
	@Transient
	@Pattern(regexp = "(^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$)|", message = "{restaurant.validation.coordinatesLongitude}")
	private String locationLongitudeAsString;

	@Transient
	private int idOfSalesPerson;

	/**
	 * Instantiates a new restaurant.
	 */
	public Restaurant() {
		this.admins = new ArrayList<>();

	}
	
	/**
	 * Gets the default logo.
	 * @return the default logo
	 */
	public RestaurantLogo getDefaultLogo() {
		if(this.restaurantLogos != null && this.restaurantLogos.size() > 0)
			defaultLogo = this.restaurantLogos.get(0);
		
		return defaultLogo;
	}

	/**
	 * Adds the offer.
	 *
	 * @param offer the offer
	 * @return the offer
	 */
	public Offer addOffer(Offer offer) {
		getOffers().add(offer);
		offer.setRestaurant(this);

		return offer;
	}

	/**
	 * Removes the offer.
	 *
	 * @param offer the offer
	 * @return the offer
	 */
	public Offer removeOffer(Offer offer) {
		getOffers().remove(offer);
		offer.setRestaurant(null);

		return offer;
	}

	/**
	 * Sets the time schedules.
	 *
	 * @param timeSchedules the new time schedules
	 */
	public void setTimeSchedules(List<TimeSchedule> timeSchedules) {
		if(this.timeSchedules == null)
			this.timeSchedules = timeSchedules;
		else
		{
			this.timeSchedules.clear();
			if(timeSchedules != null)
				this.timeSchedules.addAll(timeSchedules);
		}
	}

	/**
	 * Adds the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule addTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().add(timeSchedule);
		timeSchedule.setRestaurant(this);

		return timeSchedule;
	}

	/**
	 * Removes the time schedule.
	 *
	 * @param timeSchedule the time schedule
	 * @return the time schedule
	 */
	public TimeSchedule removeTimeSchedule(TimeSchedule timeSchedule) {
		getTimeSchedules().remove(timeSchedule);
		timeSchedule.setRestaurant(null);

		return timeSchedule;
	}

	/**
	 * Adds an admin.
	 *
	 * @param admin the admin
	 * @return the user
	 */
	public User addAdmin(User admin) {
		getAdmins().add(admin);
		admin.setRestaurant(this);

		return admin;
	}

	/**
	 * Removes an admin.
	 *
	 * @param admin the admin
	 * @return the user
	 */
	public User removeAdmin(User admin) {
		getAdmins().remove(admin);
		admin.setRestaurant(null);

		return admin;
	}

	/**
	 * Add a new donationPerMonth.
	 * @param donationPerMonth donation to add
	 * @return added donation
	 */
	public DonationPerMonth addDonationPerMonth(DonationPerMonth donationPerMonth) {
		getDonations().add(donationPerMonth);
		donationPerMonth.setRestaurant(this);

		return donationPerMonth;
	}
	
	/**
	 * Adds the restaurant logo.
	 *
	 * @param restaurantLogo the restaurant logo
	 * @return the restaurant logo
	 */
	public RestaurantLogo addRestaurantLogo(RestaurantLogo restaurantLogo) {
		
		if(null==getRestaurantLogos()){
			this.restaurantLogos = new ArrayList<RestaurantLogo>();
		}
		
		getRestaurantLogos().add(restaurantLogo);
		restaurantLogo.setRestaurant(this);

		return restaurantLogo;
	}

	/**
	 * Removes the restaurant logo.
	 *
	 * @param restaurantLogo the restaurant logo
	 * @return the restaurant logo
	 */
	public RestaurantLogo removeRestaurantLogo(RestaurantLogo restaurantLogo) {
		getRestaurantLogos().remove(restaurantLogo);
		restaurantLogo.setRestaurant(null);

		return restaurantLogo;
	}

	/**
	 * Checks if the restaurant is currently open.
	 * @return the isOpen
	 */
	public boolean getCurrentlyOpen() {
		
		int openHour = 0;
		int openMin = 0;
		int closeHour = 0;
		int closeMin = 0;
		
		for (TimeSchedule timeSchedule : this.timeSchedules) {
			Date actuallyDate = new Date();
			if(actuallyDate.getDay()+1==timeSchedule.getDayOfWeek().getDayNumber()){

				// check if there is a openingtime for the day
				List<OpeningTime> openingTimes = timeSchedule.getOpeningTimes();
				if(!openingTimes.isEmpty()){
					for(OpeningTime opening : openingTimes) {
						if(opening.getTimeSchedule().getId()==timeSchedule.getId()){
							openHour = opening.getOpeningTime().getHours();
							openMin = opening.getOpeningTime().getMinutes();
							closeHour = opening.getClosingTime().getHours();
							closeHour = opening.getClosingTime().getMinutes();
						}
						else {
							openHour = timeSchedule.getOfferStartTime().getHours();
							openMin = timeSchedule.getOfferStartTime().getMinutes();
							closeHour = timeSchedule.getOfferEndTime().getHours();
							closeMin = timeSchedule.getOfferEndTime().getMinutes();
						}
					}
				}
				else {
					openHour = timeSchedule.getOfferStartTime().getHours();
					openMin = timeSchedule.getOfferStartTime().getMinutes();
					closeHour = timeSchedule.getOfferEndTime().getHours();
					closeMin = timeSchedule.getOfferEndTime().getMinutes();
				}

				int nowHour = actuallyDate.getHours();
				int nowMin = actuallyDate.getMinutes();

				if(openHour > nowHour || (openHour == nowHour && openMin > nowMin )){
					return false;
				}

				return closeHour >= nowHour && (closeHour != nowHour || closeMin >= nowMin);
			}
		}
		return false;
	}

	public void restaurantKitchenTypesAsStringFiller() {
		kitchenTypesAsString = new ArrayList<>();

		if(kitchenTypes != null) {
			for(KitchenType kitchenType : kitchenTypes) {
				kitchenTypesAsString.add(kitchenType.getName());
			}
		}
	}

	public void orderRestaurantTimeContainers() {
		openingTimes.sort(Comparator.comparingInt(RestaurantTimeContainer::getDayNumber));
		offerTimes.sort(Comparator.comparingInt(RestaurantTimeContainer::getDayNumber));
	}

	public void restaurantTimeContainerFiller() {

		if(timeSchedules.size() > 7) {
			try {
				throw new Exception("Error - The Table Time_Schedule contains more than 7 entries per week for restaurant-ID: " + id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int dayNumber = 0;
		Date openingTime;
		Date closingTime;
		Date offerStartTime;
		Date offerEndTime;

		openingTimes = new ArrayList<>();
		offerTimes = new ArrayList<>();
		List<TimeSchedule> timeScheduleList = timeSchedules;

		for (int i = 0; i < 7; i++) {
			TimeSchedule timeSchedule = new TimeSchedule();

			try {
				timeSchedule = timeScheduleList.get(i);
			} catch (Exception e) {
				//no entry in the db
			}

			openingTime = null;
			closingTime = null;

			try {
				dayNumber = timeSchedule.getDayOfWeek().getId();
			} catch (Exception e) {
				//new time schedule
			}
			if(dayNumber == 0) {
				dayNumber = i+1;
			}

			offerStartTime = timeSchedule.getOfferStartTime();
			offerEndTime = timeSchedule.getOfferEndTime();
			offerTimes.add(new RestaurantTimeContainer(offerStartTime, offerEndTime, dayNumber));

			//The DB allows for more than one time schedule entry per day. But the FindLunchApplication only allows one pair of opening times per day.
			//The SWApp handles this similar. If there is more than one pair of opening times per day it is ignored.
			if(timeSchedule.getOpeningTimes() != null) {
				if(timeSchedule.getOpeningTimes().size() > 0) {
					openingTime = timeSchedule.getOpeningTimes().get(0).getOpeningTime();
					closingTime = timeSchedule.getOpeningTimes().get(0).getClosingTime();
				}
			}
			openingTimes.add(new RestaurantTimeContainer(openingTime, closingTime, dayNumber));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Restaurant other = (Restaurant) obj;
		if (Integer.valueOf(id) == null) {
			if (Integer.valueOf(other.id) != null)
				return false;
		}

		if (id != other.getId()) {
			return false;
		}

		if (customerId != other.getCustomerId()) {
			return false;
		}

		if (!name.equals(other.getName())) {
			return false;
		}

		if (!street.equals(other.getStreet())) {
			return false;
		}

		if (!streetNumber.equals(other.getStreetNumber())) {
			return false;
		}

		if (!zip.equals(other.getZip())) {
			return false;
		}

		if (!city.equals(other.getCity())) {
			return false;
		}

		if (!phone.equals(other.getPhone())) {
			return false;
		}

		if (!country.getCountryCode().equals(other.getCountry().getCountryCode())) {
			return false;
		}

		if (!email.equals(other.getEmail())) {
			return false;
		}

		if (locationLatitude != other.getLocationLatitude()) {
			return false;
		}


		if (locationLongitude != other.getLocationLongitude()) {
			return false;
		}

		if(url != null && other.getUrl() != null) {
			if (!url.equals(other.getUrl())) {
				return false;
			}
		}

		if (!restaurantType.getName().equals(other.getRestaurantType().getName())) {
			return false;
		}

		if (!restaurantUuid.equals(other.getRestaurantUuid())) {
			return false;
		}

		if (!offerModifyPermission == other.isOfferModifyPermission()) {
			return false;
		}

		if (!blocked == other.isBlocked()) {
			return false;
		}

		if (salesPerson.getId() != other.getSalesPerson().getId()) {
			return false;
		}

		if (!kitchenTypes.containsAll(other.getKitchenTypes())) {
			return false;
		}

		if (!courseTypeList.containsAll(other.getCourseTypeList())) {
			return false;
		}

		if (!timeSchedules.containsAll(other.getTimeSchedules())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Integer.valueOf(id) == null) ? 0 : (Integer.valueOf(id).hashCode()));

		return result;
	}

}