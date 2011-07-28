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
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.core.util;

import com.brownbag.core.util.assertion.Assert;
import com.brownbag.core.validation.AssertTrueForProperties;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class BeanPropertyType {
    private static Map<String, BeanPropertyType> cache = new HashMap<String, BeanPropertyType>();

    private BeanPropertyType parent;
    private String id;
    private Class type;
    private Class containerType;
    private Class collectionValueType;
    private Set<Annotation> annotations = new HashSet<Annotation>();

    private BeanPropertyType(BeanPropertyType parent, String id, Class type, Class containerType, Class collectionValueType) {
        this.parent = parent;
        this.id = id;
        this.type = type;
        this.containerType = containerType;
        this.collectionValueType = collectionValueType;

        initAnnotations();
    }

    private void initAnnotations() {
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(containerType, id);
        Method method = descriptor.getReadMethod();
        Annotation[] readMethodAnnotations = method.getAnnotations();
        Collections.addAll(annotations, readMethodAnnotations);

        Field field;
        try {
            field = containerType.getDeclaredField(id);
            Annotation[] fieldAnnotations = field.getAnnotations();
            Collections.addAll(annotations, fieldAnnotations);
        } catch (NoSuchFieldException e) {
            // no need to get annotations if field doesn't exist
        }
    }

    public boolean hasAssertTruePropertyDependency() {

        Method[] publicMethods = containerType.getMethods();
        Method[] declaredMethods = containerType.getDeclaredMethods();
        Set<Method> methods = new HashSet<Method>();
        Collections.addAll(methods, publicMethods);
        Collections.addAll(methods, declaredMethods);

        for (Method method : methods) {
            Annotation[] methodAnnotations = method.getAnnotations();
            for (Annotation methodAnnotation : methodAnnotations) {
                if (AssertTrueForProperties.class.isAssignableFrom(methodAnnotation.getClass())) {
                    AssertTrueForProperties assertTrueForProperties = (AssertTrueForProperties) methodAnnotation;
                    if (assertTrueForProperties.errorProperty().equals(id)) {
                        return true;
                    }
                    String[] dependentProperties = assertTrueForProperties.dependentProperties();
                    for (String dependentProperty : dependentProperties) {
                        if (dependentProperty.equals(id)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean hasAnnotation(Class annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotationClass.isAssignableFrom(annotation.getClass())) {
                return true;
            }
        }

        return false;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        for (Annotation annotation : annotations) {
            if (annotationClass.isAssignableFrom(annotation.getClass())) {
                return (T) annotation;
            }
        }

        return null;
    }

    public static BeanPropertyType getBeanPropertyType(Class clazz, String propertyPath) {
        String key = clazz.getName() + "." + propertyPath;
        if (!cache.containsKey(key)) {
            cache.put(key, getBeanPropertyTypeImpl(clazz, propertyPath));
        }

        return cache.get(key);
    }

    public static BeanPropertyType getBeanPropertyTypeImpl(Class clazz, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Class containingType;
        Class currentPropertyType = clazz;
        BeanPropertyType beanPropertyType = null;
        for (String property : properties) {
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            Assert.PROGRAMMING.assertTrue(propertyType != null && !propertyType.equals(Object.class),
                    "Invalid property path: " + clazz + "." + property);

            Class propertyPathType;
            Class collectionValueType = null;
            if (Collection.class.isAssignableFrom(propertyType)) {
                collectionValueType = ReflectionUtil.getCollectionValueType(currentPropertyType, property);
                propertyPathType = collectionValueType;
            } else {
                propertyPathType = propertyType;
            }

            containingType = currentPropertyType;
            currentPropertyType = propertyPathType;

            beanPropertyType = new BeanPropertyType(beanPropertyType, property, propertyType, containingType,
                    collectionValueType);
        }

        return beanPropertyType;
    }

    public BeanPropertyType getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public String getLeafId() {
        return StringUtil.extractAfterPeriod(id);
    }

    public Class getType() {
        return type;
    }

    public Class getContainerType() {
        return containerType;
    }

    public Class getCollectionValueType() {
        return collectionValueType;
    }

    public boolean isCollectionType() {
        return Collection.class.isAssignableFrom(type);
    }

    public boolean isValidatable() {
        BeanPropertyType beanPropertyType = parent;
        while (beanPropertyType != null) {
            try {
                Class containingType = beanPropertyType.getContainerType();
                String id = beanPropertyType.getId();

                PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(containingType, id);
                Method readMethod = descriptor.getReadMethod();
                Valid validAnnotation = null;
                if (readMethod != null) {
                    validAnnotation = readMethod.getAnnotation(Valid.class);
                }
                if (validAnnotation == null) {
                    Field field = containingType.getDeclaredField(id);
                    validAnnotation = field.getAnnotation(Valid.class);
                }

                if (validAnnotation == null) {
                    return false;
                } else {
                    beanPropertyType = beanPropertyType.getParent();
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    public BeanPropertyType getRoot() {
        if (parent == null) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }
}