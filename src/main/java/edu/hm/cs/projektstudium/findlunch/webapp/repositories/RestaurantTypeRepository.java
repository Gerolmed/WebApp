package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.RestaurantType;
import org.springframework.stereotype.Repository;;

/**
 * The Interface RestaurantTypeRepository. Abstraction for the data access layer.
 */
@Repository
public interface RestaurantTypeRepository extends JpaRepository<RestaurantType, Integer>{

	/**
	 * Find all RestaurantTypes and order them by name ascending.
	 *
	 * @return the list of RestaurantTypes (ordered by name ascending)
	 */
	List<RestaurantType> findAllByOrderByNameAsc();

	/**
	 * Find restaurant types with a specific name.
	 * @param name the restaurant type's name
	 * @return the restaurant type
	 */
	RestaurantType findByName(String name);
}
