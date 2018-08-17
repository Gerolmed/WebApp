package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Additive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The Interface AdditivesRepository. Abstraction for the data access layer.
 */
@Repository
public interface AdditiveRepository extends JpaRepository<Additive, Integer>{

	/**
	 * Finds additives by the name of the product.
	 * @param name the name of the product
	 * @return The found additives.
	 */
	Additive findByName(String name);

	/**
	 * Finds additives by the key of the product.
	 * @param key the key of the product
	 * @return The found additives.
	 */
	Additive findByShortKey(String key);

	/**
	 * Finds additives by the id of the product.
	 * @param id the id of the product
	 * @return The found additives.
	 */
	Additive findById(int id);
}
