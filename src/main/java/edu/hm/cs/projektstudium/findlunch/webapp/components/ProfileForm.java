package edu.hm.cs.projektstudium.findlunch.webapp.components;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Country;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfilPassword;
import edu.hm.cs.projektstudium.findlunch.webapp.model.validation.profile.ProfileEmailUnique;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

/**
 * Profile form of the sales person.
 */
@Getter
@Setter
public class ProfileForm {

    private int id;

    @Pattern(regexp = "^[a-zA-ZäöüÄÖÜ]{3,60}$", message = "{profile.validation.firstName}")
    private String firstName;

    @Pattern(regexp = "^[a-zA-ZäöüÄÖÜ]{3,60}$", message = "{profile.validation.secondName}")
    private String secondName;

    @Pattern(regexp = "^[a-zA-ZäöüÄÖÜ.]{4,60}$", message = "{profile.validation.street}")
    private String street;

    @Pattern(regexp = "^[^0]{1,6}$", message = "{profile.validation.streetNumber}")
    private String streetNumber;

    @Pattern(regexp = "^[0-9]{5}$", message = "{profile.validation.zip}")
    private String zip;

    @Pattern(regexp = "^[a-zA-ZäöüÄÖÜ]{3,60}$", message = "{profile.validation.city}")
    private String city;

    private Country country;

    @Pattern(regexp = "^[0][0-9/. \\-]{6,60}$", message = "{profile.validation.phone}")
    private String phone;

    @Pattern(regexp = ".+@.+\\..+", message = "{universal.validation.pattern.email}")
    @ProfileEmailUnique
    private String email;

    @Pattern(regexp = "^[A-Z]{2}([0-9a-zA-Z]{15,31})$" , message = "{profile.validation.iban}")
    private String iban;

    @Pattern(regexp = "^([a-zA-Z]){4}([a-zA-Z]){2}([0-9a-zA-Z]){2}([0-9a-zA-Z]{3})?$" , message = "{profile.validation.bic}")
    private String bic;

    @ProfilPassword
    private String validPassword;

    private String newPassword;

    private String repeatNewPassword;

    public ProfileForm() {
        super();
    }

    /**
     * Form of the sales person.
     * @param salesPerson The sales person
     */
    public ProfileForm(SalesPerson salesPerson) {
       this.id = salesPerson.getId();
       this.firstName = salesPerson.getFirstName();
       this.secondName = salesPerson.getSecondName();
       this.street = salesPerson.getStreet();
       this.streetNumber = salesPerson.getStreetNumber();
       this.zip = salesPerson.getZip();
       this.city = salesPerson.getCity();
       this.phone = salesPerson.getPhone();
       this.country = salesPerson.getCountry();
       this.email = salesPerson.getEmail();
       this.iban = salesPerson.getIban();
       this.bic = salesPerson.getBic();
    }

}
