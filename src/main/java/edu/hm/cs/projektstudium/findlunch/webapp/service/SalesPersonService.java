package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;

/**
 * Interface for services related to sales persons.
 */
public interface SalesPersonService {

    SalesPerson getSalesPersonByEmail(String email);
}
