package com.brownbag.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * User: Juan
 * Date: 5/13/11
 * Time: 12:08 AM
 */
public class StringUtil {

    public static String extractAfterPeriod(String str) {
        int periodIndex = str.indexOf(".");
        if (periodIndex < 0) {
            return str;
        } else {
            return str.substring(periodIndex + 1);
        }
    }

    public static String humanizeCamelCase(String camelCase) {
        String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        String joined = StringUtils.join(camelCaseParts, " ");
        return capitaliseFirstLetter(joined);
    }

    public static String capitaliseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isEmpty(Object s) {
        return s == null || s.toString().length() == 0;
    }
}
