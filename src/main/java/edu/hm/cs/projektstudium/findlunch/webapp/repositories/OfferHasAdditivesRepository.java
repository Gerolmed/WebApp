package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferHasAdditive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;

/**
 * The Interface OfferHasAdditivesRepository. Abstraction for the data access layer.
 */
public interface OfferHasAdditivesRepository extends JpaRepository<OfferHasAdditive, Serializable> {

    @Modifying
    @Query("delete from OfferHasAdditive where offer_id = ?1")
    void deleteByOfferId(int offerId);
}