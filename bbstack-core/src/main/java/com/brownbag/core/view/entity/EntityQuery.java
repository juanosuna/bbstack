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
import java.util.List;

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

    public Integer getLastResult() {
        return Math.min(firstResult + pageSize, resultCount.intValue());
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

    public boolean hasNextPage() {
        return Math.min(firstResult + pageSize, Math.max(resultCount.intValue() - pageSize, 0)) > firstResult;
    }

    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    public boolean hasPreviousPage() {
        return Math.max(firstResult - pageSize, 0) < firstResult;
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
