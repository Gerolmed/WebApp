package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.SalesPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the interface SalesPersonService.
 */
@Service
public class SalesPersonServiceImpl implements SalesPersonService {

    @Autowired
    private DbReaderServiceImpl dbReaderService;

    @Override
    public SalesPerson getSalesPersonByEmail(String email) {
        return dbReaderService.getSalesPersonByEmail(email);
    }
}
