package edu.hm.cs.projektstudium.findlunch.webapp.model;

import java.util.ArrayList;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.*;

import edu.hm.cs.projektstudium.findlunch.webapp.components.RestaurantTimeContainer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import com.fasterxml.jackson.annotation.JsonView;

import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.OfferView;
import edu.hm.cs.projektstudium.findlunch.webapp.controller.view.ReservationView;
import org.springframework.web.multipart.MultipartFile;


/**
 * Describes a food offer.
 */
@Entity
@ApiModel(
		description = "Beschreibt ein Essensangebot."
)
@Getter
@Setter
public class Offer {

	/** The id. */
	@ApiModelProperty(notes = "ID")
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	private int id;

	/** The description. */
	@ApiModelProperty(notes = "Beschreibung")
	@Lob
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	@NotBlank(message="{offer.description.notBlank}")
	@Size(min=2, max=500, message= "{offer.description.lengthInvalid}")
	private String description;

	/** The preparation time. */
	@ApiModelProperty(notes = "Zubereitungszeit")
	@Column(name="preparation_time")
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	@Min(value=1, message="{offer.preparationTime.invalidMinValue}")
	private int preparationTime;

	/** The price. */
	@ApiModelProperty(notes = "Preis")
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	@NumberFormat(style=Style.DEFAULT)
	@DecimalMin(value="0.5", message="{offer.price.invalidMinValue}")
	private double price;

	/** The start date. */
	@ApiModelProperty(notes = "Beginn")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	@Column(name="start_date")
	@NotNull(message="{offer.startDate.notNull}")
	private Date startDate;

	/** The end date. */
	@ApiModelProperty(notes = "Angebotsende")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="dd.MM.yyy")
	@Column(name="end_date")
	@NotNull(message="{offer.endDate.notNull}")
	private Date endDate;

	/** The title. */
	@ApiModelProperty(notes = "Bezeichnung")
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	@NotBlank(message="{offer.title.notBlank}")
	@Size(min=2, max=60, message= "{offer.title.lengthInvalid}")
	private String title;

	/** The default photo. */
	@ApiModelProperty(notes = "Foto")
	@Transient
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	private OfferPhoto defaultPhoto;
	
	/** The needed point*/
	@ApiModelProperty(notes = "Anzahl benötigter Punkte")
	@JsonView({OfferView.OfferRest.class, ReservationView.ReservationRest.class})
	@Min(value=1, message="{offer.neededPoints.invalidMinValue}")
	private int neededPoints;
	
	/** The offers within the reservation */
	@ApiModelProperty(notes = "Angebote innerhalb der Bestellung")
	@OneToMany(mappedBy="offer", cascade=CascadeType.ALL)
	private List<ReservationOffers> reservation_offers;

	/** The day of weeks. */
	//bi-directional many-to-many association to DayOfWeek
	@ApiModelProperty(notes = "Die Tage, für die das Angebot gilt.")
	@ManyToMany
	@JoinTable(
		name="offer_has_day_of_week"
		, joinColumns={
			@JoinColumn(name="offer_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="day_of_week_id")
			}
		)
	private List<DayOfWeek> dayOfWeeks;

	/** Additive. */
	//bi-directional many-to-many association to additives
	@ApiModelProperty(notes = "Eine Liste aus natürlichen oder künstlichen Additiven," +
			" die im Gericht enthalten sein könnten.")
	@JsonView(OfferView.OfferRest.class)
	@ManyToMany
	@JoinTable(
		name="offer_has_additives"
		, joinColumns={
			@JoinColumn(name="offer_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="additives_id")
			}
		)
	private List<Additive> additives;

	/** Allergenic. */
	//bi-directional many-to-many association to additives
	@ApiModelProperty(notes = "Eine Liste aus möglichen Allergenen, die im Gericht enthalten sein könnten.")
	@JsonView(OfferView.OfferRest.class)
	@ManyToMany
	@JoinTable(
		name="offer_has_allergenic"
		, joinColumns={
			@JoinColumn(name="offer_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="allergenic_id")
			}
		)
	private List<Allergenic> allergenic;

	/** The restaurant. */
	//bi-directional many-to-one association to Restaurant
	@ApiModelProperty(notes = "Das Restaurant, das das Angebot bereitstellt.")
    @JoinColumn(name="restaurant_id")
	@ManyToOne(fetch=FetchType.EAGER)
	@JsonView(OfferView.OfferRest.class)
	private Restaurant restaurant;
	
	/** The coursetype */
	@ApiModelProperty(notes = "Typ des Essenangebots.")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name="course_type")
	private CourseType courseType;

	// Für die Findlunch Webapp
	public int getCourseTypeId() {
		return courseType.getId();
	}
	
	/** The order of the offer within the coursetype */
	@ApiModelProperty(notes = "Hilfsattribut zur Sortierung.")
	@Column(name="sort")
	private int order;
	
	/** The offer photos. */
	//bi-directional many-to-one association to OfferPhoto
	@ApiModelProperty(notes = "Assoziation zu einem Foto für das Angebot.")
	@OneToMany(mappedBy="offer", cascade=CascadeType.ALL, orphanRemoval=true )
	private List<OfferPhoto> offerPhotos;

	/** Is sold out */
	@ApiModelProperty(notes = "Gibt an, ob der Angebotsvorrat ausverkauft ist.")
	@Column(name="sold_out")
	@JsonView(OfferView.OfferRest.class)
	private boolean soldOut;

	@ApiModelProperty(notes = "Die Id vom Änderungsantrag des Anbieters.")
	@Column(name = "swa_change_request_id")
	private int changeRequestId;

	@ApiModelProperty(notes = "Kommentar des Verrtriebsmitarbeiters bezüglich der letzten Änderung.")
	@Column(name = "swa_comment_of_last_change")
	private String commentOfLastChange;

	@ApiModelProperty(notes = "Der letzte Vertriebsmitarbeiter, der eine Änderung durchgeführt hat.")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "swa_last_changed_by_sales_person_id")
	private SalesPerson salesPerson;

	@Column(name = "swa_change_request")
	private boolean changeRequest;

	// Regex-Source: https://stackoverflow.com/questions/8937408/regular-expression-for-date-format-dd-mm-yyyy-in-javascript
	@Pattern(regexp = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)", message = "{offer.validation.startDate}")
	@Transient
	private String startDateAsString;

	// Regex-Source: https://stackoverflow.com/questions/8937408/regular-expression-for-date-format-dd-mm-yyyy-in-javascript
	@Pattern(regexp = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)", message = "{offer.validation.endDate}")
	@Transient
	private String endDateAsString;

	@Transient
	private String courseTypeAsString;

	@Transient
	private List<String> additivesAsString;

	@Transient
	private List<String> allergenicsAsString;

	@Transient
	private List<RestaurantTimeContainer> offerTimes;

	@Transient
	private List<String> validnessDaysOfWeekAsString;

	//The number of images per offer is limited to three
	@Transient
	private MultipartFile firstOfferImage;

	@Transient
	private MultipartFile secondOfferImage;

	@Transient
	private MultipartFile thirdOfferImage;

	@Size(min=0, max=65535, message = "{offer.validation.changeComment}")
	@Transient
	private String newChangeComment;

	@Transient
	private boolean keepFirstImage; //used in the offerChangeRequest

	@Transient
	private boolean keepSecondImage;

	@Transient
	private boolean keepThirdImage;

	@Pattern(regexp = "^[1-9][0-9]{0,2}$", message = "{offer.validation.neededPoints}")
	@Transient
	private String neededPointsAsString;

	@Transient
	private int idOfRestaurant;

	// Regex-Source: http://www.regexpal.com/93999
	// The bean-validation via regex requires a String but it is much better than the standard @Digits validation.
	@Pattern(regexp = "(\\d+\\.\\d{1,2})", message = "{offer.validation.price}")
	@Transient
	private String priceAsString;

	// The bean-validation via regex requires a String but it is much better than the standard @Digits validation.
	@Pattern(regexp = "^[0-9]{1,2}$", message = "{offer.validation.preparationTime}")
	@Transient
	private String preparationTimeAsString;

	/**
	 * Instantiates a new offer.
	 */
	public Offer() {
		
		this.offerPhotos = new ArrayList<>();
	}

	//Loads the value of the "offerHasAdditives/Allergenics" in different format which is better to handle with Thymeleaf.
	public void allergenicFiller() {
		allergenicsAsString = new ArrayList<>();

		if(allergenic != null) {
			for(Allergenic offerHasAllergenic : allergenic) {
				allergenicsAsString.add(offerHasAllergenic.getName());
			}
		}
	}

	public void additivesFiller() {
		additivesAsString = new ArrayList<>();

		if(additives != null) {
			for(Additive offerHasAdditive : additives) {
				additivesAsString.add(offerHasAdditive.getName());
			}
		}
	}

	//changes the the date format (String) from 2017-08-31 (yyyy-mm-dd) to 31-08-2017 (dd-mm-yyyy) to better match the regex validation
	public String reOrderDate(String date) {
		String year = date.substring(0,4);
		String month = date.substring(5,7);
		String day = date.substring(8,10);

		return day + "-" + month + "-" + year;
	}

	public void daysOfWeekAsStringFiller() {
		validnessDaysOfWeekAsString = new ArrayList<String>();

		if(dayOfWeeks != null) {
			for(DayOfWeek dayOfWeek : dayOfWeeks) {
				validnessDaysOfWeekAsString.add(String.valueOf(dayOfWeek.getId()));
			}
		}
	}

	public void offerTimesContainerFiller(Restaurant restaurant) {
		List<TimeSchedule> timeScheduleList = new ArrayList<TimeSchedule>();

		try {
			timeScheduleList = restaurant.getTimeSchedules();
		} catch (Exception e) {
			try {
				throw new Exception("Error - The Restaurant (ID: " + restaurant.getId() + ") has no time schedule.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		int dayNumber = 0;
		Date offerStartTime;
		Date offerEndTime;

		offerTimes = new ArrayList<RestaurantTimeContainer>();

		for (int i = 0; i < 7; i++) {
			TimeSchedule timeSchedule = new TimeSchedule();

			try {
				timeSchedule = timeScheduleList.get(i);
			} catch (Exception e) {
				//no entry in the db
			}

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
		}
	}

	/**
	 * Adds the offer photo.
	 *
	 * @param offerPhoto the offer photo
	 * @return the offer photo
	 */
	public OfferPhoto addOfferPhoto(OfferPhoto offerPhoto) {
		getOfferPhotos().add(offerPhoto);
		offerPhoto.setOffer(this);

		return offerPhotos.get(offerPhotos.size() - 1);
	}

	/**
	 * Removes the offer photo.
	 *
	 * @param offerPhoto the offer photo
	 * @return the offer photo
	 */
	public OfferPhoto removeOfferPhoto(OfferPhoto offerPhoto) {
		getOfferPhotos().remove(offerPhoto);
		offerPhoto.setOffer(null);

		return offerPhoto;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Offer other = (Offer) obj;
        if(Integer.valueOf(id) == null) {
            if (Integer.valueOf(other.id) != null)
                return false;
        }

        if (id != other.getId()) {
            return false;
        }

        if (changeRequestId != other.getChangeRequestId()) {
            return false;
        }

        int restaurantId = restaurant.getId();
        int otherRestaurantId = other.getRestaurant().getId();
        if (restaurantId != otherRestaurantId) {
            return false;
        }

        if(!title.equals(other.getTitle())) {
            return false;
        }

        if(!description.equals(other.getDescription())) {
            return false;
        }

        if(!(Double.compare(price, other.getPrice()) == 0)) {
            return false;
        }

        if(preparationTime != other.getPreparationTime()) {
            return false;
        }

        if(startDate.compareTo(other.getStartDate()) != 0) {
            return false;
        }

        if(endDate.compareTo(other.getEndDate()) != 0) {
            return false;
        }

        if(neededPoints != other.getNeededPoints()) {
            return false;
        }

        if(soldOut != other.isSoldOut()) {
            return false;
        }

        if(courseType != null && other.getCourseType() != null) {
            if(courseType.getId() != other.getCourseType().getId()) {
                return false;
            }
        }

        if(commentOfLastChange != null && other.getCommentOfLastChange() != null) {
            if (!commentOfLastChange.equals(other.getCommentOfLastChange())) {
                return false;
            }
        }

        if(salesPerson != null && other.getSalesPerson() != null) {
            if (salesPerson.getId() != other.getSalesPerson().getId()) {
                return false;
            }
        }

        //DayOfWeeks check
        if(dayOfWeeks != null && other.getDayOfWeeks() != null) {
            List<Integer> dayOfWeeksIds = new ArrayList<Integer>();
            List<Integer> otherDayOfWeeksIds = new ArrayList<Integer>();

            for(DayOfWeek dayOfWeek : dayOfWeeks) {
                dayOfWeeksIds.add(dayOfWeek.getId());
            }

            for(DayOfWeek dayOfWeek : other.getDayOfWeeks()) {
                otherDayOfWeeksIds.add(dayOfWeek.getId());
            }

            if(!dayOfWeeksIds.containsAll(otherDayOfWeeksIds)) {
                return false;
            }
        }

        if(dayOfWeeks == null && other.getDayOfWeeks() != null || dayOfWeeks != null && other.getDayOfWeeks() == null) {
            return false;
        }

        //OfferHasAdditives check
        if(additives != null && other.getAdditives() != null) {
            List<Integer> offerHasAdditivesIds = new ArrayList<Integer>();
            List<Integer> otherOfferHasAdditivesIds = new ArrayList<Integer>();

            for(Additive offerHasAdditive : additives) {
                offerHasAdditivesIds.add(offerHasAdditive.getId());
            }

            for(Additive offerHasAdditive : other.getAdditives()) {
                otherOfferHasAdditivesIds.add(offerHasAdditive.getId());
            }

            if(!offerHasAdditivesIds.containsAll(otherOfferHasAdditivesIds)) {
                return false;
            }
        }

        if(additives == null && other.getAdditives() != null || additives != null && other.getAdditives() == null) {
            return false;
        }

        //Allergenic check
        if(allergenic != null && other.getAllergenic() != null) {
            List<Integer> offerHasAllergenicsIds = new ArrayList<Integer>();
            List<Integer> otherOfferHasAllergenicsIds = new ArrayList<Integer>();

            for(Allergenic offerHasAllergenic : allergenic) {
                offerHasAllergenicsIds.add(offerHasAllergenic.getId());
            }

            for(Allergenic offerHasAllergenic : other.getAllergenic()) {
                otherOfferHasAllergenicsIds.add(offerHasAllergenic.getId());
            }

            if(!offerHasAllergenicsIds.containsAll(otherOfferHasAllergenicsIds)) {
                return false;
            }
        }

        if(allergenic == null && other.getAllergenic() != null || allergenic != null && other.getAllergenic() == null) {
            return false;
        }

        //Offer photos check
        if(offerPhotos != null && other.getOfferPhotos() != null) {
            List<Integer> offerPhotosIds = new ArrayList<Integer>();
            List<Integer> otherOfferPhotosIds = new ArrayList<Integer>();

            for(OfferPhoto offerPhoto : offerPhotos) {
                offerPhotosIds.add(offerPhoto.getId());
            }

            for(OfferPhoto offerPhoto : other.getOfferPhotos()) {
                if(offerPhoto.getBase64Encoded() != null) {
                    otherOfferPhotosIds.add(offerPhoto.getId());
                }
            }

            if(!offerPhotosIds.containsAll(otherOfferPhotosIds)) {
                return false;
            }
        }

        if(offerPhotos == null && other.getOfferPhotos() != null || offerPhotos != null && other.getOfferPhotos() == null) {
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