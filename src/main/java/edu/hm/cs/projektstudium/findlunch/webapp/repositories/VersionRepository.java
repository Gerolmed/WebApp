package edu.hm.cs.projektstudium.findlunch.webapp.repositories;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for the versions
 */
@Repository
public interface VersionRepository extends JpaRepository<Version, Integer> {

	/**
	 * Gets a specific version
	 * @param versionId the ID
	 * @return the version
	 */
	Version findById(int versionId);
	
	/**
	 * Gets a version for a version name
	 * @param versionName the version
	 * @return the Version
	 */
	Version findByVersionName(String versionName);
	
}
