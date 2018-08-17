package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Platform;
import edu.hm.cs.projektstudium.findlunch.webapp.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for the platforms
 */
@Repository
public interface PlatformRepository extends JpaRepository<Platform, Integer> {

	/**
	 * Gets a specific version
	 * @param platformId the ID
	 * @return the version
	 */
	Platform findById(int platformId);
	
	/**
	 * Gets a platform for a platform name
	 * @param platformName the platform
	 * @return the Platform
	 */
	Platform findByPlatformName(String platformName);
	
}
