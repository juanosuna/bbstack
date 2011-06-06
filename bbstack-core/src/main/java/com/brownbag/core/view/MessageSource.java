package com.brownbag.core.view;

import com.brownbag.core.view.MainApplication;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * User: Juan
 * Date: 5/11/11
 * Time: 5:17 PM
 */
public class MessageSource extends ReloadableResourceBundleMessageSource {

    public String getMessageWithDefault(String code) {
        return getMessage(code, null, code);
    }

    public String getMessage(String code) {
        Locale locale;
        if (MainApplication.getInstance() == null) {
            locale = Locale.getDefault();
        } else {
            locale = MainApplication.getInstance().getLocale();
        }
        return getMessage(code, null, null, locale);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, code);
    }

    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        Locale locale;
        if (MainApplication.getInstance() == null) {
            locale = Locale.getDefault();
        } else {
            locale = MainApplication.getInstance().getLocale();
        }
        return getMessage(code, args, defaultMessage, locale);
    }
}
