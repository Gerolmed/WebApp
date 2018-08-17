package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Reservation;

/**
 * The Interface ReservationRepository. Abstraction for the data access layer
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	
	/**
	 * Find all Reservation from a user after a given date.
	 * @param userId Id of the user
	 * @param midnight date
	 * @param statuskey the status key
	 * @return List of reservation
	 */
	List<Reservation> findByUserIdAndTimestampReceivedAfterAndReservationStatusKeyAndPointsCollectedFalse(int userId, Date midnight, int statuskey);
	
	/**
	 * Find all reservations that are not confirmed and after the given date.
	 * @param restaurantId Id of restaurant
	 * @param statuskey the status key
	 * @param reservationTime date
	 * @return List of reservation
	 */
	List<Reservation> findByRestaurantIdAndReservationStatusKeyAndTimestampReceivedAfter(int restaurantId, int statuskey, Date reservationTime);
	
	/**
	 * Find all reservations between the given dates.
	 * @param restaurantId Id of the restaurant
	 * @param reservationTimeStart start time of reservation
	 * @param reservationTimeEnd end time of reservation
	 * @return reservations for restaurants in time frame, ordered ascending
	 */
	List<Reservation> findByRestaurantIdAndTimestampReceivedBetweenOrderByTimestampReceivedAsc(int restaurantId, Date reservationTimeStart, Date reservationTimeEnd);
	
	
	/**
	 * Find all reservations that are confirmed or rejected and between the given dates.
	 * @param restaurantId Id of the restaurant
	 * @param statuskey The status key.
	 * @param reservationTimeStart start time of reservation
	 * @param reservationTimeEnd end time of reservation
	 * @return confirmed and rejected reservations for restaurants in the given time frame, ordered ascending
	 */
	List<Reservation> findByRestaurantIdAndReservationStatusKeyNotAndTimestampReceivedBetweenOrderByTimestampReceivedAsc(int restaurantId, int statuskey, Date reservationTimeStart, Date reservationTimeEnd);
	
	/**
	 * Find all confirmed reservations from a restaurant.
	 * @param restaurantId Id of restaurant
	 * @param statuskey the status key
	 * @param billId Id of bill
	 * @return List of reservation (order by reservationTime ascending)
	 */
	List<Reservation> findByRestaurantIdAndReservationStatusKeyAndBillIdOrderByTimestampReceivedAsc(int restaurantId, int statuskey, Integer billId);
	
	/**
	 * Find all reservations which are new, nor rejected neighter confirmed and not paid by points.
	 * @param userId Id of the customer
	 * @param midnight date
	 * @param statuskey the status key
	 * @return List of corresponding reservations
	 */
	List<Reservation> findByUserIdAndTimestampReceivedAfterAndUsedPointsFalseAndReservationStatusKey(int userId, Date midnight, int statuskey);

	/**
	 * Find all reservations that are between the given dates.
	 * @param restaurantId Id of the restaurant
	 * @param reservationTimeAfter Time after reservation
	 * @param reservationTimeBefore Time before reservation
	 * @return List of reservations between the timestamp
	 */
	List<Reservation> findByRestaurantIdAndTimestampReceivedBetween(int restaurantId, Date reservationTimeAfter, Date reservationTimeBefore);
	
	/**
	 * Find all reservations from a restaurant for the given user.
	 * @param userId Id of the user
	 * @return List of reservations 
	 */
	List<Reservation> findByUserIdOrderByRestaurantIdAscTimestampReceivedAsc(int userId);

	/**
	 * Find all reservations paid using PayPal that have not yet been settled or voided.
	 * @return List of transactions not yet settled/voided
	 */
	List<Reservation> findByUsedPaypalTrueAndPpTransactionIdNotNullAndPpTransactionFinishedFalse();

    List<Reservation> findByRestaurantId(int restaurantId);
}
