package com.airing.spring.cloud.base.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceUtils {
    @Autowired
    private MessageSource messageSource;

    public String getByKey(String key, String[] args) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public String getByKey(String key, String[] args, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

}
