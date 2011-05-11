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

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 10:36 PM
 */
@Service
public class Validation<T> {
    private Validator validator;

    public Validation() {
        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }
}
