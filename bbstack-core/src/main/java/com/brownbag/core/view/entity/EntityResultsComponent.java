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
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

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

    EntityQuery getEntityQuery() {
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
        buttonPanel = createButtonPanel();
        resultsPanel.addComponent(buttonPanel);
        resultsPanel.addComponent(entityTable);

        setCompositionRoot(resultsPanel);
        search();
    }

    private Panel createButtonPanel() {
        Panel buttonPanel = new Panel();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
        buttonPanel.setSizeUndefined();
        buttonPanel.addStyleName(Runo.PANEL_LIGHT);
        buttonPanel.setContent(layout);

        Button firstButton = new Button(uiMessageSource.getMessage("entityResults.first"), getEntityTable(), "firstPage");
        buttonPanel.addComponent(firstButton);

        Button previousButton = new Button(uiMessageSource.getMessage("entityResults.previous"), getEntityTable(), "previousPage");
        buttonPanel.addComponent(previousButton);

        Button nextButton = new Button(uiMessageSource.getMessage("entityResults.next"), getEntityTable(), "nextPage");
        buttonPanel.addComponent(nextButton);

        Button lastButton = new Button(uiMessageSource.getMessage("entityResults.last"), getEntityTable(), "lastPage");
        buttonPanel.addComponent(lastButton);

        Label pageSizeLabel = new Label(uiMessageSource.getMessage("entityResults.pageSize") + ": ");
        buttonPanel.addComponent(pageSizeLabel);
        ComboBox pageSizeMenu = new ComboBox();
        pageSizeMenu.addItem(10);
        pageSizeMenu.addItem(25);
        pageSizeMenu.addItem(50);
        pageSizeMenu.addItem(100);
        MethodProperty pageProperty = new MethodProperty(getEntityQuery(), "pageSize");
        pageSizeMenu.setPropertyDataSource(pageProperty);
        pageSizeMenu.setNullSelectionAllowed(false);
        pageSizeMenu.setImmediate(true);
        pageSizeMenu.setWidth(5, UNITS_EM);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        buttonPanel.addComponent(pageSizeMenu);

        return buttonPanel;
    }

    public void addSelectionChangedListener(Object target, String methodName) {
        entityTable.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        Object itemId = getEntityTable().getValue();
        BeanItem beanItem = getEntityTable().getContainerDataSource().getItem(itemId);
        return beanItem.getBean();
    }

    public void search() {
        getEntityTable().search();
        String caption = uiMessageSource.getMessage("entityResults.caption",
                new Object[]{getEntityQuery().getResultCount()});
        getEntityTable().setCaption(caption);
    }
}
