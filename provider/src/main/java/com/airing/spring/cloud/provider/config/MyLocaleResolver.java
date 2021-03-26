package com.airing.spring.cloud.provider.config;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class MyLocaleResolver implements LocaleResolver {

    private static final String LANG_HEAD = "lang";
    private static final String LANG_PARAM = "lang";
    private static final String DEFAULT_LANG = "en_US";
    private static final String LANG_SPLIT = "_";

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getHeader(LANG_HEAD);
        if (lang == null || lang.length() == 0) {
            lang = request.getParameter(LANG_PARAM);
        }
        if (lang == null || lang.length() == 0) {
            lang = DEFAULT_LANG;
        }
        String[] language = lang.split(LANG_SPLIT);
        Locale locale = new Locale(language[0], language[1]);
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
