package com.brownbag.core.util;

import com.brownbag.core.util.assertion.Assert;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BeanPropertyType {
    private BeanPropertyType parent;
    private String id;
    private Class type;
    private Class containerType;
    private Class collectionValueType;
    private Set<Annotation> annotations = new HashSet<Annotation>();

    public BeanPropertyType(BeanPropertyType parent, String id, Class type, Class containerType, Class collectionValueType) {
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
        String[] properties = propertyPath.split("\\.");
        Class containingType;
        Class currentPropertyType = clazz;
        BeanPropertyType beanPropertyType = null;
        for (String property : properties) {
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            Assert.PROGRAMMING.assertTrue(propertyType != null && !propertyType.equals(Object.class),
                    "Invalid property path:" + clazz + "." + property);

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
                Field field = containingType.getDeclaredField(id);
                Valid validAnnotation = field.getAnnotation(Valid.class);
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