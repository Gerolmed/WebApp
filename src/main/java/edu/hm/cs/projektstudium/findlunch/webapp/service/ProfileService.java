package edu.hm.cs.projektstudium.findlunch.webapp.service;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;

/**
 * Interface for services related to profile services.
 */
public interface ProfileService {

    void addSalesPersonToTransactionStore(SalesPerson salesPerson);

    boolean salesPersonHasBeenAlteredMeanwhile(int salesPersonId);

    void saveSalesPerson(ProfileForm profileForm);

    SalesPerson getSalesByEmail(String email);

    boolean emailOfSalesPersonHasBeenAltered(ProfileForm profileForm);

    void setNewPassword(ProfileForm profileForm);
}
