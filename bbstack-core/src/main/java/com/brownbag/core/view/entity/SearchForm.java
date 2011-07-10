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

import com.brownbag.core.view.entity.field.FormFields;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class SearchForm<T> extends FormComponent<T> {

    private EntityQuery entityQuery;

    EntityQuery getEntityQuery() {
        return entityQuery;
    }

    void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    @Override
    public void postConstruct() {
        super.postConstruct();

        getForm().addStyleName("searchForm");

        BeanItem beanItem = createBeanItem(getEntityQuery());
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

    @Override
    protected void createFooterButtons(HorizontalLayout footerLayout) {
        footerLayout.setSpacing(true);
        footerLayout.setMargin(true);

        Button clearButton = new Button(uiMessageSource.getMessage("entitySearchForm.clear"), this, "clear");
        clearButton.setDescription(uiMessageSource.getMessage("entitySearchForm.clear.description"));
        clearButton.setIcon(new ThemeResource("icons/16/clear.png"));
        clearButton.addStyleName("small default");
        footerLayout.addComponent(clearButton);
        // alignment doesn't work
//        footerLayout.setComponentAlignment(clearButton, Alignment.MIDDLE_RIGHT);

        Button searchButton = new Button(uiMessageSource.getMessage("entitySearchForm.search"), this, "search");
        searchButton.setDescription(uiMessageSource.getMessage("entitySearchForm.search.description"));
        searchButton.setIcon(new ThemeResource("icons/16/search.png"));
        searchButton.addStyleName("small default");
        footerLayout.addComponent(searchButton);
//        footerLayout.setComponentAlignment(searchButton, Alignment.MIDDLE_RIGHT);
    }

    @Override
    FormFields createFormFields() {
        return new FormFields(getEntityType(), entityMessageSource, false);
    }

    public void clear() {
        getEntityQuery().clear();
        BeanItem beanItem = createBeanItem(getEntityQuery());
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());

        getResults().search();
    }

    public void search() {
        getForm().commit();
        getResults().search();
    }
}
