package com.vaadin.data.util;

import java.util.Collection;

/**
 * User: Juan
 * Date: 5/30/11
 * Time: 5:38 PM
 */
public class NullCapableBeanItemContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {
    private Class beanType;

    public NullCapableBeanItemContainer(Class<? super BEANTYPE> type) throws IllegalArgumentException {
        super(type);
        beanType = type;
    }

    public NullCapableBeanItemContainer(Class<? super BEANTYPE> type, Collection<? extends BEANTYPE> beantypes) throws IllegalArgumentException {
        super(type, beantypes);
        beanType = type;
    }

    @Override
    public boolean addNestedContainerProperty(String propertyId) {
        return addContainerProperty(propertyId, new NullCapableNestedPropertyDescriptor(
                propertyId, beanType));
    }
}
