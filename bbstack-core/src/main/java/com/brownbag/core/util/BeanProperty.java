package com.brownbag.core.util;

import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.lang.reflect.Field;

public class BeanProperty {
    private BeanProperty parent;
    private String id;
    private Class type;
    private Class containerType;

    public BeanProperty(BeanProperty parent, String id, Class type, Class containerType) {
        this(id, type, containerType);
        this.parent = parent;
    }

    private BeanProperty(String id, Class type, Class containerType) {
        this.id = id;
        this.type = type;
        this.containerType = containerType;
    }

    public static BeanProperty getBeanProperty(Class clazz, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Class containingType;
        Class currentPropertyType = clazz;
        String propertyId;
        BeanProperty beanProperty = null;
        for (String property : properties) {
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            if (propertyType == null || propertyType.equals(Object.class)) {
                throw new RuntimeException("Invalid property path given for class " + clazz
                        + ": " + property);
            } else {
                containingType = currentPropertyType;
                currentPropertyType = propertyType;
                propertyId = property;

                beanProperty = new BeanProperty(beanProperty, propertyId, currentPropertyType, containingType);
            }
        }

        return beanProperty;
    }

    public BeanProperty getParent() {
        return parent;
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

    public boolean isValidationOn() {
        BeanProperty beanProperty = parent;
        while (beanProperty != null) {
            try {
                Class containingType = beanProperty.getContainerType();
                String id = beanProperty.getId();
                Field field = containingType.getDeclaredField(id);
                Valid validAnnotation = field.getAnnotation(Valid.class);
                if (validAnnotation == null) {
                    return false;
                } else {
                    beanProperty = beanProperty.getParent();
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}