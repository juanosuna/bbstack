package com.vaadin.data.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * User: Juan
 * Date: 5/30/11
 * Time: 5:38 PM
 */
public class NullCapableBeanItemContainer<BEANTYPE> extends BeanItemContainer<BEANTYPE> {
    private Class beanType;
    private Set<String> nonSortablePropertyIds = new HashSet<String>();

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

    public Set<String> getNonSortablePropertyIds() {
        return nonSortablePropertyIds;
    }

    public void setNonSortablePropertyIds(Set<String> nonSortablePropertyIds) {
        this.nonSortablePropertyIds = nonSortablePropertyIds;
    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        LinkedList<Object> sortables = new LinkedList<Object>();
        for (Object propertyId : getContainerPropertyIds()) {
            if (!nonSortablePropertyIds.contains(propertyId)) {
                sortables.add(propertyId);
            }
        }
        return sortables;
    }
}
