package com.brownbag.core.util;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * User: Juan
 * Date: 7/12/11
 * Time: 3:51 PM
 */
public class CurrencyUtil {

    public static List<Currency> getAvailableCurrencies() {
        List<Currency> currencies = new ArrayList<Currency>();
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (!StringUtil.isEmpty(locale.getCountry())) {
                Currency currency = Currency.getInstance(locale);
                currencies.add(currency);
            }
        }

        return currencies;
    }
}
