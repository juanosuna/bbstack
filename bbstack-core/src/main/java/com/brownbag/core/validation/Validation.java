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
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 10:36 PM
 */
@Service
public class Validation {
    private Validator validator;
    private ValidatorFactory factory;

    public Validation() {
        factory = javax.validation.Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public ValidatorFactory getFactory() {
        return factory;
    }

    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    private <T> ConstraintViolation findNotNullViolation(Set<ConstraintViolation<T>> violations) {
        for (ConstraintViolation violation : violations) {
            if (violation.getConstraintDescriptor().getAnnotation().annotationType().equals(NotNull.class)) {
                return violation;
            }
        }

        return null;
    }

    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyPath, Class<?>... groups) {

        Set<ConstraintViolation<T>> violations = new HashSet<ConstraintViolation<T>>();

        try {
            int currentIndex = -1;
            String currentPropertyPath = propertyPath;

            do {
                currentIndex = propertyPath.indexOf(".", ++currentIndex);
                if (currentIndex >= 0) {
                    currentPropertyPath = propertyPath.substring(0, currentIndex);
                } else {
                    currentPropertyPath = propertyPath;
                }

                Set<ConstraintViolation<T>> currentViolations = validator.validateProperty(object, currentPropertyPath, groups);

                ConstraintViolation<T> notNullViolation = findNotNullViolation(currentViolations);
                if (notNullViolation == null) {
                    violations = currentViolations;
                } else {
                    violations.add(notNullViolation);
                    break;
                }
            } while (currentIndex >= 0);
        } catch (IllegalArgumentException e) {
            System.out.print(e.getMessage());
            // ignore null property path
        }

        return violations;
    }

    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return validator.validateValue(beanType, propertyName, value, groups);
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return validator.getConstraintsForClass(clazz);
    }

    public <T> T unwrap(Class<T> type) {
        return validator.unwrap(type);
    }

}
