package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Allergenic;

/**
 * The Interface AllergenicRepository. Abstraction for the data access layer.
 */

@Repository
public interface AllergenicRepository extends JpaRepository<Allergenic, Integer>{

	/**
	 * Finds allerginics by the name of the product.
	 * @param name the name of the product
	 * @return The found allergenics.
	 */
	Allergenic findByName(String name);
	
	/**
	 * Finds allerginics by the key of the product.
	 * @param key the key of the product
	 * @return The found allergenics.
	 */
	Allergenic findByShortKey(String key);
	
	/**
	 * Finds allerginics by the id of the product.
	 * @param id the id of the product
	 * @return The found allergenics.
	 */
	Allergenic findById(int id);
	
}
