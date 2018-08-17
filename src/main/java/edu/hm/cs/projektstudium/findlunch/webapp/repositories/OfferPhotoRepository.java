package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.OfferPhoto;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The Interface OfferPhotoRepository. Abstraction for the data access layer.
 */
public interface OfferPhotoRepository extends JpaRepository<OfferPhoto, Integer>{
	
	/**
	 * Find all OfferPhoto by a given offer id.
	 *
	 * @param offerId the offer id
	 * @return the list of OfferPhoto for the offer id
	 */
	List<OfferPhoto> findByOffer_id(int offerId);

	@Modifying
	@Query("delete from OfferPhoto where offer_id = ?1")
	void deleteByOfferId(int offerId);

	@Modifying
	@Query("delete from OfferPhoto where id = ?1")
	void deleteById(int offerPhotoId);

	OfferPhoto findById(int offerPhotoId);
}
