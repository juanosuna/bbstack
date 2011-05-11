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
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.data.util.POJOItem;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntitySearchForm<T> extends Form {

    private EntityQuery entityQuery;

    private EntityResults entityResults;

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityQuery getEntityQuery() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityQuery.class, getEntityType());
    }

    public EntityResults getEntityResults() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityResults.class, getEntityType());
    }

    @PostConstruct
    public void postConstruct() {
        entityQuery = getEntityQuery();
        entityResults = getEntityResults();

        GridLayout gridLayout = new GridLayout(getFormConfig().getColumns(), getFormConfig().getRows());
        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);
        setLayout(gridLayout);

        setWriteThrough(true);
        setInvalidCommitted(true);
        setImmediate(true);

        initButtons();
        init();

        POJOItem personQueryItem = new POJOItem(entityQuery, getFormConfig().getPropertyIds());
        super.setItemDataSource(personQueryItem, getFormConfig().getPropertyIds());
    }

    private void initButtons() {

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        Button clear = new Button("Clear", this, "clear");
        buttons.addComponent(clear);

        Button search = new Button("Search", this, "search");
        buttons.addComponent(search);

        getFooter().setMargin(true);
        getFooter().addComponent(buttons);
    }

    public void clear() {
        entityQuery.clear();
        setComponentError(null);
        POJOItem personQueryItem = new POJOItem(entityQuery, getFormConfig().getPropertyIds());
        super.setItemDataSource(personQueryItem, getFormConfig().getPropertyIds());

        entityResults.search();
    }

    public void search() {
        try {
            commit();
            // hack, not sure why this is happening , since invalid data is allowed
        } catch (com.vaadin.data.Validator.InvalidValueException e) {
        }
        entityResults.search();
    }

    public abstract FormConfig getFormConfig();

    public abstract void init();

    @Override
    protected void attachField(Object propertyId, Field field) {
        GridLayout gridLayout = (GridLayout) getLayout();
        FormConfig formConfig = getFormConfig();
        if (formConfig.containsProperty(propertyId.toString())) {
            Integer columnStart = formConfig.getFieldConfig(propertyId.toString()).getColumnStart();
            Integer rowStart = formConfig.getFieldConfig(propertyId.toString()).getRowStart();
            Integer columnEnd = formConfig.getFieldConfig(propertyId.toString()).getColumnEnd();
            Integer rowEnd = formConfig.getFieldConfig(propertyId.toString()).getRowEnd();
            if (columnEnd != null && rowEnd != null) {
                gridLayout.addComponent(field, columnStart, rowStart, columnEnd, rowEnd);
            } else {
                gridLayout.addComponent(field, columnStart, rowStart);
            }
        }
    }
}
