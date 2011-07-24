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

package com.vaadin.data.util;

import com.vaadin.data.Property;

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
