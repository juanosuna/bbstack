package com.brownbag.core.util;

import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import java.lang.reflect.Field;

public class BeanPropertyType {
    private BeanPropertyType parent;
    private String id;
    private Class type;
    private Class containerType;

    public BeanPropertyType(BeanPropertyType parent, String id, Class type, Class containerType) {
        this(id, type, containerType);
        this.parent = parent;
    }

    private BeanPropertyType(String id, Class type, Class containerType) {
        this.id = id;
        this.type = type;
        this.containerType = containerType;
    }

    public static BeanPropertyType getBeanProperty(Class clazz, String propertyPath) {
        String[] properties = propertyPath.split("\\.");
        Class containingType;
        Class currentPropertyType = clazz;
        String propertyId;
        BeanPropertyType beanPropertyType = null;
        for (String property : properties) {
            Class propertyType = BeanUtils.findPropertyType(property, new Class[]{currentPropertyType});
            if (propertyType == null || propertyType.equals(Object.class)) {
                throw new RuntimeException("Invalid property path given for class " + clazz
                        + ": " + property);
            } else {
                containingType = currentPropertyType;
                currentPropertyType = propertyType;
                propertyId = property;

                beanPropertyType = new BeanPropertyType(beanPropertyType, propertyId, currentPropertyType, containingType);
            }
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