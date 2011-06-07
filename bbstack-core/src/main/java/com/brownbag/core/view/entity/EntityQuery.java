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

package com.brownbag.core.view.entity;

import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
public abstract class EntityQuery<T> {

    private Integer pageSize = 10;
    private Integer firstResult = 0;
    private Long resultCount = 0L;
    private String orderByPropertyId;
    private OrderDirection orderDirection = OrderDirection.ASC;
    private Set<String> nonSortablePropertyIds = new HashSet<String>();

    public abstract List<T> execute();

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public Long getResultCount() {
        return resultCount;
    }

    public void setResultCount(Long resultCount) {
        this.resultCount = resultCount;
    }

    public void firstPage() {
        firstResult = 0;
    }

    public void nextPage() {
        firstResult = Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0));
    }

    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    public void lastPage() {
        firstResult = Math.max(resultCount.intValue() - pageSize, 0);
    }

    public String getOrderByPropertyId() {
        if (orderByPropertyId == null) {
            return null;
        } else {
            return orderByPropertyId.toString();
        }
    }

    public void setOrderByPropertyId(String orderByPropertyId) {
        this.orderByPropertyId = orderByPropertyId;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public boolean isSortable(String propertyId) {
        return !nonSortablePropertyIds.contains(propertyId);
    }

    public void setSortable(String propertyId, boolean isSortable) {
        if (nonSortablePropertyIds.contains(propertyId) && isSortable) {
            nonSortablePropertyIds.remove(propertyId);
        } else if (!isSortable) {
            nonSortablePropertyIds.add(propertyId);
        }
    }

    public void clear() {
        setOrderByPropertyId(null);
        setOrderDirection(OrderDirection.ASC);

        try {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(this);
            for (PropertyDescriptor descriptor : descriptors) {
                Method writeMethod = descriptor.getWriteMethod();
                if (writeMethod != null && !writeMethod.getDeclaringClass().equals(EntityQuery.class)
                        && !writeMethod.getDeclaringClass().equals(Object.class)) {
                    Class type = descriptor.getPropertyType();
                    if (type.isPrimitive() && !type.isArray()) {
                        if (Number.class.isAssignableFrom(type)) {
                            writeMethod.invoke(this, 0);
                        } else if (Boolean.class.isAssignableFrom(type)) {
                            writeMethod.invoke(this, false);
                        }
                    } else {
                        writeMethod.invoke(this, new Object[]{null});
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "EntityQuery{" +
                "pageSize=" + pageSize +
                ", firstResult=" + firstResult +
                '}';
    }

    public enum OrderDirection {
        ASC,
        DESC
    }
}
