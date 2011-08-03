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

package com.brownbag.core.validation;


import com.brownbag.core.util.ExceptionUtil;
import com.brownbag.core.util.ReflectionUtil;
import com.vaadin.data.Validator;

import java.text.Format;
import java.text.ParsePosition;

public class ConversionValidator implements Validator {
    private Class type;
    private Format format;

    public ConversionValidator(Class type) {
        this.type = type;
    }

    public ConversionValidator(Class type, Format format) {
        this.type = type;
        this.format = format;
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        try {
            Object parsedValue;
            if (format == null) {
                parsedValue = value;
            } else {
                ParsePosition parsePosition = new ParsePosition(0);
                parsedValue = format.parseObject(value.toString(), parsePosition);
                if (value.toString().length() > parsePosition.getIndex() || parsePosition.getErrorIndex() >= 0) {
                    throw new InvalidValueException("Not a valid number");
                }
            }
            ReflectionUtil.convertValue(parsedValue, type);
        } catch (Exception e) {
            NumberFormatException numberFormatException = ExceptionUtil.findThrowableInChain(e, NumberFormatException.class);
            if (numberFormatException != null) {
                throw new InvalidValueException("Not a valid number");
            } else {
                if (e.getCause() != null) {
                    throw new InvalidValueException(e.getCause().getMessage());
                } else {
                    throw new InvalidValueException(e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean isValid(Object value) {
        try {
            validate(value);
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }
}
