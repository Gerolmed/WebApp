package edu.hm.cs.projektstudium.findlunch.webapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Defines if an offer contains allergenics.
 */
@Entity
@Table(name = "offer_has_allergenic")
@Getter
@Setter
public class OfferHasAllergenic {

	/**
	 * The id.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * The offer.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    /**
     * The allergenics.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "allergenic_id")
    private Allergenic allergenic;

    public OfferHasAllergenic() { super(); }
}
