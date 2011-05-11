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

package com.brownbag.core.query;

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
    private Object orderByField;
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

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
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
        firstResult = Math.min(firstResult + pageSize, resultCount.intValue() - pageSize);
    }

    public void previousPage() {
        firstResult = Math.max(firstResult - pageSize, 0);
    }

    public void lastPage() {
        firstResult = resultCount.intValue() - pageSize;
    }

    public String getOrderByField() {
        if (orderByField == null) {
            return null;
        } else {
            return orderByField.toString();
        }
    }

    public void setOrderByField(Object orderByField) {
        this.orderByField = orderByField;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public abstract void clear();

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
