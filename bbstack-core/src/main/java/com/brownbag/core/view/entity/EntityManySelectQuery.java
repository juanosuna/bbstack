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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
@Component
@Scope("session")
public abstract class EntityManySelectQuery<T, P> extends EntityQuery<T> {

    private P parent;

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    @Override
    public void clear() {
        setParent(null);
        setOrderByField(null);
        setOrderDirection(OrderDirection.ASC);
    }

    @Override
    public String toString() {
        return "EntityManySelectQuery{" +
                "parent='" + parent +
                '}';
    }
}
