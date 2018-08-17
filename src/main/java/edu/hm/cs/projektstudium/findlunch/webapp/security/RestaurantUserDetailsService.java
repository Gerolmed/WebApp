package edu.hm.cs.projektstudium.findlunch.webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.User;
import edu.hm.cs.projektstudium.findlunch.webapp.repositories.UserRepository;

/**
 * This class is responsible for getting users with type "Anbieter" for authentication purposes (website).
 */
@Service("restaurantUserDetailsService")
public class RestaurantUserDetailsService implements UserDetailsService {

	/** The user repository. */
	@Autowired
	private UserRepository userRepository;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	/**
	 * Loads user depending on the username, throws an exception if the username was not found.
	 * 
	 * @param username the user name
	 * @return the user name and type
	 */
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		if(username.equals("owner@owner.com")) return userRepository.findByUsernameAndUserType_name(username,"Betreiber");
			
		return userRepository.findByUsernameAndUserType_name(username,"Anbieter");
	}
}
