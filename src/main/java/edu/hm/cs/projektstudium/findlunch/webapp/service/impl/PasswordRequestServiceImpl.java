package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.components.PasswordMapContainer;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordRequestService;
import org.joda.time.DateTime;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * Implementation of the interface PasswordRequestService.
 */
@Service
public class PasswordRequestServiceImpl implements PasswordRequestService {

    private static HashMap<String, PasswordMapContainer> resetCodes = new HashMap<String, PasswordMapContainer>();
    private ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder();

    @Override
    public String createPasswordResetCode(String userEmail) {
        DateTime dateTime = DateTime.now();
        String resetCode = shaPasswordEncoder.encodePassword(dateTime.toString() + userEmail, null);
        PasswordMapContainer container = new PasswordMapContainer(userEmail, dateTime);
        resetCodes.put(resetCode, container);

        return resetCode;
    }

    @Override
    public String getUserEmail(String resetCode) {
        PasswordMapContainer container = resetCodes.get(resetCode);
        return container.getUserEmail();
    }

    @Override
    public void deletePasswordResetCode(String resetCode) {
        resetCodes.remove(resetCode);
    }

    @Override
    public HashMap<String, PasswordMapContainer> getResetCodes() {
        return resetCodes;
    }


}