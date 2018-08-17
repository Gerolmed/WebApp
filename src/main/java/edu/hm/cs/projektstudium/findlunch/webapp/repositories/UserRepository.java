package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import edu.hm.cs.projektstudium.findlunch.webapp.model.User;

import java.util.List;

/**
 * The Interface UserRepository. Abstraction for the data access layer.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	/**
	 * Find a User by its username.
	 *
	 * @param username the name of the user
	 * @return the user
	 */
	public User findByUsername(String username);
	
	/**
	 * Find a User by its username and a user type.
	 *
	 * @param username the name of the user
	 * @param userType the user type (as string)
	 * @return the user
	 */
	public User findByUsernameAndUserType_name(String username, String userType);

	/**
	 * Get a List of Users filtered by enabled push notification.
	 *
	 * @return the filtered list of users
	 */
	public List<User> findAllByPushNotificationEnabledIsTrue();
}
