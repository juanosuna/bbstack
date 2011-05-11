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

import com.brownbag.core.ui.EntityFieldFactory;
import com.brownbag.core.util.ReflectionUtil;
import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.ui.Field;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 2:19 AM
 */
public class BeanValidationUtil {
    public static Set<BeanValidationValidator> addValidators(EntityFieldFactory entityFieldFactory, Class clazz) {
        Set<BeanValidationValidator> validators = new HashSet<BeanValidationValidator>();
        Set<Object> propertyIds = entityFieldFactory.getPropertyIds();
        for (Object propertyId : propertyIds) {
            Field field = entityFieldFactory.getField(propertyId);
            BeanValidationValidator validator = addValidator(field, propertyId, clazz);
            validators.add(validator);
        }

        return validators;
    }

    public static BeanValidationValidator addValidator(Field field, Object propertyId, Class clazz) {
        ReflectionUtil.BeanProperty beanProperty = ReflectionUtil.getType(clazz, propertyId.toString());

        return BeanValidationValidator.addValidator(field, beanProperty.getId(), beanProperty.getContainerType());
    }

}
