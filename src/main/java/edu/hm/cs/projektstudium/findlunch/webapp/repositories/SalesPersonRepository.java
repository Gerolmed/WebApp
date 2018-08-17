package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * The Interface SalesPersonRepository. Abstraction for the data access layer.
 */
@Repository
public interface SalesPersonRepository extends JpaRepository<SalesPerson, Serializable> {

	/**
	 * Finds a sales person by email.
	 * @param userEmail email of the sales person
	 * @return the sales person
	 */
    SalesPerson findByEmail(String userEmail);

    /**
	 * Finds a sales person by id.
	 * @param id id of the sales person
	 * @return the sales person
	 */
    SalesPerson findById(int id);
}
