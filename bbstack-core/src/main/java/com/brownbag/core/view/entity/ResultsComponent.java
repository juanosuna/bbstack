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
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.view.MessageSource;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class ResultsComponent<T> extends CustomComponent {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource(name = "entityMessageSource")
    private MessageSource entityMessageSource;

    private EntityDao entityDao;
    private ResultsTable resultsTable;
    private EntityForm entityForm;
    private EntityQuery entityQuery;
    private DisplayFields displayFields;

    private HorizontalLayout resultsButtons;

    protected ResultsComponent() {
    }

    public abstract void configureFields(DisplayFields displayFields);

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityDao getEntityDao() {
        return entityDao;
    }

    void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public ResultsTable getResultsTable() {
        return resultsTable;
    }

    public EntityForm getEntityForm() {
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

    public HorizontalLayout getResultsButtons() {
        return resultsButtons;
    }

    public void postConstruct() {
        displayFields = new DisplayFields(getEntityType(), entityMessageSource);
        configureFields(displayFields);
        resultsTable = new ResultsTable(this);
        resultsTable.setSizeUndefined();

        VerticalLayout verticalLayout = new VerticalLayout();
        setCompositionRoot(verticalLayout);

        resultsButtons = createResultsButtons();
        addComponent(resultsButtons);

        addComponent(resultsTable);

        setCustomSizeUndefined();
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    private HorizontalLayout createResultsButtons() {
        HorizontalLayout navigationButtons = new HorizontalLayout();
        navigationButtons.setMargin(false);
        navigationButtons.setSpacing(true);

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
        pageSizeMenu.setWidth(4, UNITS_EM);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        navigationButtons.addComponent(pageSizeMenu);
        Label pageSizeLabel = new Label(uiMessageSource.getMessage("entityResults.pageSize"));
        pageSizeLabel.setSizeUndefined();
        pageSizeLabel.addStyleName("small");
        navigationButtons.addComponent(pageSizeLabel);

        Button firstButton = new Button(null, getResultsTable(), "firstPage");
        firstButton.setSizeUndefined();
        firstButton.addStyleName("borderless");
        firstButton.setIcon(new ThemeResource("icons/16/first.png"));
        navigationButtons.addComponent(firstButton);

        Button previousButton = new Button(null, getResultsTable(), "previousPage");
        previousButton.setSizeUndefined();
        previousButton.addStyleName("borderless");
        previousButton.setIcon(new ThemeResource("icons/16/previous.png"));
        navigationButtons.addComponent(previousButton);

        Button nextButton = new Button(null, getResultsTable(), "nextPage");
        nextButton.setSizeUndefined();
        nextButton.addStyleName("borderless");
        nextButton.setIcon(new ThemeResource("icons/16/next.png"));
        navigationButtons.addComponent(nextButton);

        Button lastButton = new Button(null, getResultsTable(), "lastPage");
        lastButton.setSizeUndefined();
        lastButton.addStyleName("borderless");
        lastButton.setIcon(new ThemeResource("icons/16/last.png"));
        navigationButtons.addComponent(lastButton);

        HorizontalLayout resultsButtons = new HorizontalLayout();
        resultsButtons.setWidth("100%");
        resultsButtons.setMargin(true, false, false, false);
        resultsButtons.addStyleName("resultsButtons");
        resultsButtons.addComponent(navigationButtons);
        resultsButtons.setComponentAlignment(navigationButtons, Alignment.MIDDLE_RIGHT);

        return resultsButtons;
    }

    public int getPageSize() {
        return getEntityQuery().getPageSize();
    }

    public void setPageSize(int pageSize) {
        getEntityQuery().setPageSize(pageSize);
        getResultsTable().setPageLength(pageSize);
    }

    public void addSelectionChangedListener(Object target, String methodName) {
        resultsTable.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public Object getSelectedValue() {
        return getResultsTable().getValue();
    }

    public Collection getSelectedValues() {
        return (Collection) getResultsTable().getValue();
    }

    public void search() {
        searchImpl(true);
    }

    protected void searchImpl(boolean clearSelection) {
        getEntityQuery().firstPage();
        getResultsTable().executeCurrentQuery();

        String caption = uiMessageSource.getMessage("entityResults.caption",
                new Object[]{getEntityQuery().getResultCount()});
        getResultsTable().setCaption(caption);
        if (clearSelection) {
            getResultsTable().clearSelection();
        }
    }
}
