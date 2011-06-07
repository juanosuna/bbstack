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
import com.vaadin.data.util.NullCapableBeanItemContainer;
import com.vaadin.ui.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public class ResultsTable extends Table {

    private ResultsComponent results;

    protected ResultsTable(ResultsComponent results) {
        this.results = results;
        addStyleName("strong striped");
        postConstruct();
    }

    public DisplayFields getEntityFields() {
        return results.getDisplayFields();
    }

    public Class getEntityType() {
        return results.getEntityType();
    }

    public void postConstruct() {
        BeanItemContainer dataSource = new NullCapableBeanItemContainer(getEntityType());
        String[] propertyIds = getEntityFields().getPropertyIdsAsArray();
        for (String propertyId : propertyIds) {
            dataSource.addNestedContainerProperty(propertyId);
        }
        setContainerDataSource(dataSource);

        setSelectable(true);
        setImmediate(true);
        setPageLength(10);
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
            if (results.getEntityQuery().isSortable(propertyId.toString())) {
                results.getEntityQuery().setOrderByPropertyId(propertyId[0].toString());
                if (ascending[0]) {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.ASC);
                } else {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.DESC);
                }
                firstPage();
            } else {
                throw new UnsupportedOperationException("No sorting on this column");
            }
        }
    }

    public void firstPage() {
        clearSelection();
        results.getEntityQuery().firstPage();
        executeCurrentQuery();
    }

    public void previousPage() {
        clearSelection();
        results.getEntityQuery().previousPage();
        executeCurrentQuery();
    }

    public void nextPage() {
        clearSelection();
        results.getEntityQuery().nextPage();
        executeCurrentQuery();
    }

    public void lastPage() {
        clearSelection();
        results.getEntityQuery().lastPage();
        executeCurrentQuery();
    }

    public void executeCurrentQuery() {
        List entities = results.getEntityQuery().execute();
        getContainerDataSource().removeAllItems();
        for (Object entity : entities) {
            getContainerDataSource().addBean(entity);
        }
    }

    public void clearSelection() {
        if (isMultiSelect()) {
            setValue(new HashSet());
        } else {
            setValue(null);
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
