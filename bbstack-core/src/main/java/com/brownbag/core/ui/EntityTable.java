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

package com.brownbag.core.ui;

import com.brownbag.core.query.EntityQuery;
import com.brownbag.core.util.CollectionsUtil;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.POJOContainer;
import com.vaadin.ui.Table;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityTable<T> extends Table {

    private EntityQuery entityQuery;

    protected EntityTable() {
    }

    protected EntityTable(String caption) {
        super(caption);
    }

    protected EntityTable(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public abstract LinkedHashMap getFields();

    public EntityQuery getEntityQuery() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityQuery.class, getEntityType());
    }

    @PostConstruct
    public void init() {
        entityQuery = getEntityQuery();

        Container dataSource = new POJOContainer(getEntityType(), CollectionsUtil.toStringArray(getFields().keySet()));
        setContainerDataSource(dataSource);

        setSelectable(true);
        setImmediate(true);
        setPageLength(0);
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setCacheRate(1);

        setVisibleColumns(getFields().keySet().toArray());
        setColumnHeaders(CollectionsUtil.toStringArray(getFields().values()));
    }

    @Override
    public POJOContainer getContainerDataSource() {
        return (POJOContainer) super.getContainerDataSource();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
        if (propertyId.length > 1) {
            throw new RuntimeException("Cannot sort on more than one column");
        } else if (propertyId.length == 1) {
            entityQuery.setOrderByField(propertyId[0]);
            if (ascending[0]) {
                entityQuery.setOrderDirection(EntityQuery.OrderDirection.ASC);
            } else {
                entityQuery.setOrderDirection(EntityQuery.OrderDirection.DESC);
            }
            search();
        }
    }

    public void firstPage() {
        entityQuery.firstPage();
        executeQuery();
    }

    public void previousPage() {
        entityQuery.previousPage();
        executeQuery();
    }

    public void nextPage() {
        entityQuery.nextPage();
        executeQuery();
    }

    public void lastPage() {
        entityQuery.lastPage();
        executeQuery();
    }

    public void search() {
        entityQuery.setFirstResult(0);
        executeQuery();
    }

    public void executeQuery() {
        List entities = entityQuery.execute();
        getContainerDataSource().removeAllItems();
        for (Object entity : entities) {
            getContainerDataSource().addPOJO(entity);
        }
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
        // Format by property type
        if (property.getType() == Date.class) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            return dateFormat.format((Date) property.getValue());
        }

        return super.formatPropertyValue(rowId, colId, property);
    }
}
