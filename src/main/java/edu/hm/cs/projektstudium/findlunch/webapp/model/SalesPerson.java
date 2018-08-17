package edu.hm.cs.projektstudium.findlunch.webapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Defines a sales person.
 */
@Entity
@Table(name = "swa_sales_person")
@Getter
@Setter
public class SalesPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    private String street;

    @Column(name = "street_number")
    private String streetNumber;

    private String zip;

    private String city;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "country_code")
    private Country country;

    private String email;

    private String iban;

    private String bic;

    /**
     * Salary percentage of the sales person.
     */
    @Column(name = "salary_percentage")
    private Double salaryPercentage;

    /**
     * List of restaurants a sales person is responsible for.
     */
    @OneToMany(mappedBy = "salesPerson", fetch = FetchType.LAZY)
    private List<Restaurant> restaurants;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        SalesPerson other = (SalesPerson) obj;
        if (Integer.valueOf(id) == null) {
            if (Integer.valueOf(other.id) != null)
                return false;
        }

        if (id != other.getId()) {
            return false;
        }

        if (!password.equals(other.getPassword())) {
            return false;
        }

        if (!firstName.equals(other.getFirstName())) {
            return false;
        }

        if (!secondName.equals(other.getSecondName())) {
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

        if (!iban.equals(other.getIban())) {
            return false;
        }

        if (!bic.equals(other.getBic())) {
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
