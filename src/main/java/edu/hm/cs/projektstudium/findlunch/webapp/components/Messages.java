package edu.hm.cs.projektstudium.findlunch.webapp.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * Class to display messages to a sales person.
 */
@Component
public class Messages {

    @Autowired
    private MessageSource messageSource;

    private MessageSourceAccessor messageSourceAccessor;

    @PostConstruct
    private void init() {
        messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    public String getMessage(String code, Locale locale) {
        return messageSourceAccessor.getMessage(code, locale);
    }

}
