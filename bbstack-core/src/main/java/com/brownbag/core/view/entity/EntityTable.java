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

import com.brownbag.core.view.entity.field.DisplayFields;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MyBeanItemContainer;
import com.vaadin.ui.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public class EntityTable extends Table {

    private EntityResultsComponent entityResults;

    protected EntityTable(EntityResultsComponent entityResults) {
        this.entityResults = entityResults;
        addStyleName("strong striped");
        postConstruct();
    }

    public EntityQuery getEntityQuery() {
        return entityResults.getEntityQuery();
    }

    public DisplayFields getEntityFields() {
        return entityResults.getDisplayFields();
    }

    public Class getEntityType() {
        return entityResults.getEntityType();
    }

    public void postConstruct() {
        BeanItemContainer dataSource = new MyBeanItemContainer(getEntityType());
        String[] propertyIds = getEntityFields().getPropertyIdsAsArray();
        for (String propertyId : propertyIds) {
            dataSource.addNestedContainerProperty(propertyId);
        }
        setContainerDataSource(dataSource);

        setSelectable(true);
        setImmediate(true);
        setPageLength(0);
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setCacheRate(1);

        setVisibleColumns(getEntityFields().getPropertyIdsAsArray());
        setColumnHeaders(getEntityFields().getLabelsAsArray());
    }

    @Override
    public BeanItemContainer getContainerDataSource() {
        return (BeanItemContainer) super.getContainerDataSource();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
        if (propertyId.length > 1) {
            throw new RuntimeException("Cannot sort on more than one column");
        } else if (propertyId.length == 1) {
            if (!entityResults.getDisplayFields().getField(propertyId[0].toString()).isDerived()) {
                getEntityQuery().setOrderByField(propertyId[0]);
                if (ascending[0]) {
                    getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.ASC);
                } else {
                    getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.DESC);
                }
                search();
            } else {
                throw new UnsupportedOperationException("No sorting on this column");
            }
        }
    }

    public void firstPage() {
        getEntityQuery().firstPage();
        executeQuery();
    }

    public void previousPage() {
        getEntityQuery().previousPage();
        executeQuery();
    }

    public void nextPage() {
        getEntityQuery().nextPage();
        executeQuery();
    }

    public void lastPage() {
        getEntityQuery().lastPage();
        executeQuery();
    }

    public void search() {
        getEntityQuery().firstPage();
        executeQuery();
    }

    public void executeQuery() {
        List entities = getEntityQuery().execute();
        getContainerDataSource().removeAllItems();
        for (Object entity : entities) {
            getContainerDataSource().addBean(entity);
        }
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
        // Format by property type
        if (property.getType() == Date.class) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // todo make locale specific
            return dateFormat.format((Date) property.getValue());
        }

        return super.formatPropertyValue(rowId, colId, property);
    }
}
