package com.airing.spring.cloud.base.config;

import com.airing.spring.cloud.base.Constant;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 自定义本地化解析策略
 *
 * @author GEYI
 * @date 2021年03月31日 10:04
 */
public class MyLocaleResolver implements LocaleResolver {

    private static final String LANG_PARAM = "lang";
    private static final String DEFAULT_LANG = "en_US";

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang = request.getHeader(Constant.LANGUAGE_HEAD);
        if (lang == null || lang.length() == 0) {
            lang = request.getParameter(LANG_PARAM);
        }
        if (lang == null || lang.length() == 0) {
            lang = DEFAULT_LANG;
        }
        String[] language = lang.split(Constant.LANG_SPLIT);
        Locale locale = new Locale(language[0], language[1]);
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
