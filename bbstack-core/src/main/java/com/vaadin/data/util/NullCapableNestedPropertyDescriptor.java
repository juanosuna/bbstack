package com.vaadin.data.util;

import com.vaadin.data.Property;

/**
 * User: Juan
 * Date: 5/28/11
 * Time: 12:42 AM
 */
public class NullCapableNestedPropertyDescriptor<BT> implements VaadinPropertyDescriptor<BT> {
    private final String name;
    private final Class<?> propertyType;

    public NullCapableNestedPropertyDescriptor(String name, Class<BT> beanType)
            throws IllegalArgumentException {
        this.name = name;
        NullCapableNestedMethodProperty property = new NullCapableNestedMethodProperty(beanType, name);
        this.propertyType = property.getType();
    }

    public String getName() {
        return name;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public Property createProperty(BT bean) {
        return new NullCapableNestedMethodProperty(bean, name);
    }
}
