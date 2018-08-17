package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.service.DbWriterService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordRequestService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the interface PasswordResetService.
 */
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordRequestService passwordRequestService;

    @Autowired
    private DbWriterService dbWriterService;

    private ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);

    @Override
    public void setNewPassword(String newPassword, String securityCode) {

        String userEmail =  passwordRequestService.getUserEmail(securityCode);
        dbWriterService.setNewPassword(userEmail, shaPasswordEncoder.encodePassword(newPassword, null));
        passwordRequestService.deletePasswordResetCode(securityCode);
    }
}

