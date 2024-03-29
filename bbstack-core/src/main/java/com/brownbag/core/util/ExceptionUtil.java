/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.core.util;

/**
 * User: Juan
 * Date: 8/2/11
 */
public class ExceptionUtil {

    public static <T> T findThrowableInChain(Throwable throwable, Class<? extends Throwable> throwableClass) {
        Throwable currentThrowable = throwable;

        while (currentThrowable != null) {
            if (throwableClass.isAssignableFrom(currentThrowable.getClass())) {
                return (T) currentThrowable;
            } else {
                currentThrowable = currentThrowable.getCause();
            }
        }

        return null;
    }
}
