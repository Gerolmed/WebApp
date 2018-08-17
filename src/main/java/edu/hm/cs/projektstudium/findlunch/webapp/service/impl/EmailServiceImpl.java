package edu.hm.cs.projektstudium.findlunch.webapp.service.impl;

import edu.hm.cs.projektstudium.findlunch.webapp.components.Messages;
import edu.hm.cs.projektstudium.findlunch.webapp.model.SalesPerson;
import edu.hm.cs.projektstudium.findlunch.webapp.service.DbReaderService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.EmailService;
import edu.hm.cs.projektstudium.findlunch.webapp.service.PasswordRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of the interface EmailService.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private DbReaderService dbReaderService;

    @Autowired
    private PasswordRequestService passwordRequestService;

    @Autowired
    private Messages messages;

    private SimpleMailMessage mailMessage;

    private Timer timer;

    private String userEmail;

    private Thread mailingThread;

    private boolean mailHasBeenSend;

    @Override
    public void generatePasswordRequestMail(String userEmail) {
        logger.debug("PasswordRequestForm success - User " + userEmail + " requested a PasswordResetMail.");
        this.userEmail = userEmail;
        String resetCode = passwordRequestService.createPasswordResetCode(userEmail);
        SalesPerson salesPerson = dbReaderService.getSalesPersonByEmail(userEmail);
        String localeAsString = LocaleContextHolder.getLocale().toString();
        Locale locale;

        if(localeAsString.equalsIgnoreCase("en")) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.GERMAN;
        }

        String subject = messages.getMessage("mail.text.subject", locale);
        String mailText = messages.getMessage("mail.text.hello", locale) +
                " " + salesPerson.getFirstName() + " " + salesPerson.getSecondName() + ",\n\n" +
                messages.getMessage("mail.text.mailTextPart1", locale) + "\n" + userEmail + "\n\n" +
                messages.getMessage("mail.text.mailTextPart2", locale) + "\n" + resetCode + "\n\n" +
                messages.getMessage("mail.text.mailTextPart3", locale) + "\n" +
                messages.getMessage("mail.text.mailTextPart4", locale);

        mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setFrom("no-reply@SalesWebApp.com");
        mailMessage.setSubject(subject);
        mailMessage.setText(mailText);
    }

    /* Timer explanation:
    * The Google test mail account does`t answer within 30 seconds if it is used for the first time
    * since several days. Therefore the program receives a time out. If the Google test mail account
    * is called after it has been "waken up" it answers within 3 seconds.
    * Thus the mailing process is started in its own thread and if it does`t finish within 10 seconds
    * the thread is stopped and the user receives a message in the login dialog. If the user try`s again a
    * minute later it works fine.
    *
    * To stop the mailing thread the depreciated function Thread.stop is needed. Thread.interrupt isn`t working
    * as the mailing thread needs to be stopped from outside.
     */

    public boolean sendMail() {
        timer = new Timer();
        mailHasBeenSend = false;

        mailingThread = new Thread (
                new Runnable() {
                    @Override
                    public void run() {
                        javaMailSender.send(mailMessage);
                        mailHasBeenSend = true;
                        logger.debug("A password request mail (User: " + userEmail + ") has been send.");
                    }
                }
        );

        mailingThread.start();
        timer.schedule(new StopClock(), 10 * 1000); // ~10 seconds
        try {
            Thread.sleep(6*1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mailHasBeenSend;
    }

    private class StopClock extends TimerTask {
        public void run() {
            mailingThread.stop();
            timer.cancel();
        }
    }

}
