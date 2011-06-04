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

import com.brownbag.core.dao.EntityDao;
import com.brownbag.core.util.MessageSource;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResultsComponent<T> extends CustomComponent {

    private MessageSource uiMessageSource;
    private MessageSource entityMessageSource;

    private EntityDao entityDao;
    private EntityTable entityTable;
    private EntityForm entityForm;
    private EntityQuery entityQuery;
    private DisplayFields displayFields;

    private Panel buttonPanel;

    protected EntityResultsComponent() {
    }

    public abstract void configureEntityFields(DisplayFields displayFields);

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    MessageSource getUiMessageSource() {
        return uiMessageSource;
    }

    void setUiMessageSource(MessageSource uiMessageSource) {
        this.uiMessageSource = uiMessageSource;
    }

    public void setEntityMessageSource(MessageSource entityMessageSource) {
        this.entityMessageSource = entityMessageSource;
    }

    EntityDao getEntityDao() {
        return entityDao;
    }

    void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    EntityTable getEntityTable() {
        return entityTable;
    }

    EntityForm getEntityForm() {
        return entityForm;
    }

    void setEntityForm(EntityForm entityForm) {
        this.entityForm = entityForm;
    }

    public EntityQuery getEntityQuery() {
        return entityQuery;
    }

    void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    protected Panel getButtonPanel() {
        return buttonPanel;
    }

    public void postConstruct() {
        displayFields = new DisplayFields(getEntityType(), entityMessageSource);
        configureEntityFields(displayFields);
        entityTable = new EntityTable(this);

        Panel resultsPanel = new Panel();
        resultsPanel.addStyleName("borderless");
        buttonPanel = createButtonPanel();
        resultsPanel.addComponent(buttonPanel);
        resultsPanel.addComponent(entityTable);

        setCompositionRoot(resultsPanel);
        search();
    }

    private Panel createButtonPanel() {
        Panel buttonPanel = new Panel();
        buttonPanel.addStyleName("borderless");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
//        buttonPanel.setSizeUndefined();
        buttonPanel.addStyleName(Runo.PANEL_LIGHT);
        buttonPanel.setContent(layout);

        Button firstButton = new Button(uiMessageSource.getMessage("entityResults.first"), getEntityTable(), "firstPage");
        firstButton.addStyleName("small default");
        buttonPanel.addComponent(firstButton);

        Button previousButton = new Button(uiMessageSource.getMessage("entityResults.previous"), getEntityTable(), "previousPage");
        previousButton.addStyleName("small default");
        buttonPanel.addComponent(previousButton);

        Button nextButton = new Button(uiMessageSource.getMessage("entityResults.next"), getEntityTable(), "nextPage");
        nextButton.addStyleName("small default");
        buttonPanel.addComponent(nextButton);

        Button lastButton = new Button(uiMessageSource.getMessage("entityResults.last"), getEntityTable(), "lastPage");
        lastButton.addStyleName("small default");
        buttonPanel.addComponent(lastButton);

        Label pageSizeLabel = new Label(uiMessageSource.getMessage("entityResults.pageSize") + ": ");
        pageSizeLabel.addStyleName("small");
        buttonPanel.addComponent(pageSizeLabel);
        Select pageSizeMenu = new Select();
        pageSizeMenu.addStyleName("small");
        pageSizeMenu.addItem(10);
        pageSizeMenu.addItem(25);
        pageSizeMenu.addItem(50);
        pageSizeMenu.addItem(100);
        MethodProperty pageProperty = new MethodProperty(this, "pageSize");
        pageSizeMenu.setPropertyDataSource(pageProperty);
        pageSizeMenu.setFilteringMode(Select.FILTERINGMODE_OFF);
        pageSizeMenu.setNullSelectionAllowed(false);
        pageSizeMenu.setImmediate(true);
        pageSizeMenu.setWidth(5, UNITS_EM);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        buttonPanel.addComponent(pageSizeMenu);

        return buttonPanel;
    }

    public int getPageSize() {
        return getEntityQuery().getPageSize();
    }

    public void setPageSize(int pageSize) {
        getEntityQuery().setPageSize(pageSize);
        getEntityTable().setPageLength(pageSize);
    }

    public void addSelectionChangedListener(Object target, String methodName) {
        entityTable.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        return getEntityTable().getValue();
    }

    public Collection getSelectedValues() {
        return (Collection) getEntityTable().getValue();
    }

    public void search() {
        searchImpl(true);
    }

    protected void searchImpl(boolean clearSelection) {
        getEntityTable().search();
        String caption = uiMessageSource.getMessage("entityResults.caption",
                new Object[]{getEntityQuery().getResultCount()});
        getEntityTable().setCaption(caption);
        if (clearSelection) {
            getEntityTable().clearSelection();
        }
    }
}
