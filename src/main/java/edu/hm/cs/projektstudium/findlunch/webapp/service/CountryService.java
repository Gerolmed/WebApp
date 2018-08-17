package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.Country;

import java.util.List;

/**
 * Interface for services related to the country service.
 */
public interface CountryService {

    List<Country> getAllCountries();
}
