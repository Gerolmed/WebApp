package edu.hm.cs.projektstudium.findlunch.webapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Defines if offer has additives.
 */
@Entity
@Table(name = "offer_has_additives")
@Getter
@Setter
public class OfferHasAdditive {

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
     * The additives.
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "additives_id")
    private Additive additive;

    public OfferHasAdditive() { super(); }
}
