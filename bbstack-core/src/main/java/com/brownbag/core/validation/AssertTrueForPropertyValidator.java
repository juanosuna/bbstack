/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 3:12 PM
 */
public class AssertTrueForPropertyValidator implements ConstraintValidator<AssertTrueForProperty, Object> {

    private AssertTrueForProperty assertTrueForProperty;

    @Override
    public void initialize(AssertTrueForProperty constraintAnnotation) {
        assertTrueForProperty = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Boolean isValid = (Boolean) value;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(assertTrueForProperty.message()).addConstraintViolation();
        }

        return isValid;
    }
}
