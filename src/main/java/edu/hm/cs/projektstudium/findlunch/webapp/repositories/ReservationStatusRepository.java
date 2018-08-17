package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.ReservationStatus;

/**
 * The Interface ReservationStatusRepository. Abstraction for the data access layer.
 */
@Repository
public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Integer>{

	/**
	 * Finds the reservation status by key.
	 * @param statusKey the status key
	 * @return List ReservationStatus
	 */
	List<ReservationStatus> findByKey(int statusKey);
	
	/**
	 * Finds the reservation status by id.
	 * @param id Id of the reservation status
	 * @return ReservationStatus
	 */
	ReservationStatus findById(int id);
}
