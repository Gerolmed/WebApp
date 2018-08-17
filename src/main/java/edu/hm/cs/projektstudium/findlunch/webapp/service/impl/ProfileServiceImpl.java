package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.components.ProfileForm;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbWriterService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * This class is used to handle the business logic for changes of salesPerson data/profile.
 */

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private DbWriterService dbWriterService;

    private ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);

    //The sales person`s id is the key.
    /**This Map is used to store the profile(data) of the sales person which is send to the user.
    * When the user presses save the profile is loaded  again from the DB and then compared to the
    * profile at the start of the transaction (stored in the HashMap). If the profile has been altered
    * on the server while the user worked on it, the save request is rejected. This logic is used
    * to ensure data consistency.
    * The word transaction is used twice here:
    * 1) Transaction: Start by the GET.Request for the profileForm model - End by the comparison check if the DB-Object has been altered.
    * 2) Transaction: Only used to save the profile(data) with @Transactional */
    private static HashMap<Integer, SalesPerson> salesPersonsTransactionStore = new HashMap<Integer, SalesPerson>();

    @Override
    public void addSalesPersonToTransactionStore(SalesPerson salesPerson) {
        salesPersonsTransactionStore.put(salesPerson.getId(), salesPerson);
    }

    @Override
    public boolean salesPersonHasBeenAlteredMeanwhile(int salesPersonId) {
        SalesPerson salesPersonTransactionEnd = dbReaderService.getSalesPersonById(salesPersonId);
        SalesPerson salesPersonTransactionStart = salesPersonsTransactionStore.get(salesPersonId);

        if(!salesPersonTransactionStart.equals(salesPersonTransactionEnd)) {
            return true;
        }

        return false;
    }

    @Override
    public void saveSalesPerson(ProfileForm profileForm) {
        dbWriterService.saveProfileChange(profileForm);
    }

    @Override
    public SalesPerson getSalesByEmail(String email) {
        return  dbReaderService.getSalesPersonByEmail(email);
    }

    @Override
    public boolean emailOfSalesPersonHasBeenAltered(ProfileForm profileForm) {
        String emailOfSalesPersonInDb = dbReaderService.getSalesPersonById(profileForm.getId()).getEmail();
        String emailOfSalesInSubmit = profileForm.getEmail();

        if(!emailOfSalesPersonInDb.equals(emailOfSalesInSubmit)) {
            return true;
        }

        return false;
    }

    /**
    * Saves the new password if the conditions are correct.
    * This function is always called when the user profile is saved.
    * Therefore the function needs to check for itself if a password
    * change is required.
     */
    @Override
    public void setNewPassword(ProfileForm profileForm) {

        if(!profileForm.getValidPassword().equals("")) {
            String encodedOldPassword = shaPasswordEncoder.encodePassword(profileForm.getValidPassword(), null);

            if(salesPersonsTransactionStore.get(profileForm.getId()).getPassword().equals(encodedOldPassword)) {

                if(profileForm.getNewPassword().equals(profileForm.getRepeatNewPassword())) {
                    String encodedNewPassword = shaPasswordEncoder.encodePassword(profileForm.getNewPassword(), null);
                    dbWriterService.setNewPassword(profileForm.getId(), encodedNewPassword);
                }
            }
        }
    }

}
