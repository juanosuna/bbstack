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

package com.brownbag.core.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * User: Juan
 * Date: 5/9/11
 * Time: 2:09 PM
 */
public class ReflectionUtil {
    public static Class getGenericArgumentType(Class clazz) {
        Type type = clazz.getGenericSuperclass();

        if (type != null && type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (!(type instanceof Class) || type.equals(Object.class)) {
                return null;
            } else {
                return getGenericArgumentType((Class) type);
            }
        }
    }

    public static BeanProperty getType(Class clazz, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Class containingType = null;
        Class currentPropertyType = clazz;
        String propertyId = propertyPath;
        for (int i = 0; i < properties.length; i++) {
            String property = properties[i];
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            if (propertyType == null || propertyType.equals(Object.class)) {
                throw new RuntimeException("Invalid otherProperty type " + propertyType
                        + " for otherProperty " + property);
            } else {
                containingType = currentPropertyType;
                currentPropertyType = propertyType;
                propertyId = properties[i];
            }
        }

        return new BeanProperty(propertyId, currentPropertyType, containingType);
    }

    public static class BeanProperty {
        private String id;
        private Class type;
        private Class containerType;

        private BeanProperty(String id, Class type, Class containerType) {
            this.id = id;
            this.type = type;
            this.containerType = containerType;
        }

        public String getId() {
            return id;
        }

        public Class getType() {
            return type;
        }

        public Class getContainerType() {
            return containerType;
        }
    }
}
