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

package com.brownbag.core.view.entity.field;

import com.brownbag.core.util.MethodDelegate;
import com.brownbag.core.view.MessageSource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;

import java.util.*;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:15 PM
 */
public class FormFields extends DisplayFields {

    private boolean attachValidators = false;
    private Map<String, AddRemoveMethodDelegate> optionalTabs = new HashMap<String, AddRemoveMethodDelegate>();

    public FormFields(Class type, MessageSource messageSource, boolean attachValidators) {
        super(type, messageSource);
        this.attachValidators = attachValidators;
    }

    public int getColumns() {
        return getColumns("");
    }

    public int getColumns(String tabName) {
        int columns = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            if (formField.getTabName().equals(tabName)) {
                columns = Math.max(columns, formField.getColumnStart());
                if (formField.getColumnEnd() != null) {
                    columns = Math.max(columns, formField.getColumnEnd());
                }
            }
        }

        return ++columns;
    }

    public int getRows() {
        return getRows("");
    }

    public int getRows(String tabName) {
        int rows = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            if (formField.getTabName().equals(tabName)) {
                rows = Math.max(rows, formField.getRowStart());
                if (formField.getRowEnd() != null) {
                    rows = Math.max(rows, formField.getRowEnd());
                }
            }
        }

        return ++rows;
    }

    public GridLayout createGridLayout() {
        return createGridLayout(getFirstTabName());
    }

    public String getFirstTabName() {
        return getTabNames().iterator().next();
    }

    public GridLayout createGridLayout(String tabName) {
        GridLayout gridLayout = new GridLayout(getColumns(tabName), getRows(tabName));
        gridLayout.setMargin(true, true, true, true);
        gridLayout.setSpacing(true);
        gridLayout.setSizeUndefined();

        return gridLayout;
    }

    @Override
    protected FormField createField(String propertyId) {
        return new FormField(this, propertyId);
    }

    public void setPosition(String tabName, String propertyId, int columnStart, int rowStart) {
        setPosition(tabName, propertyId, columnStart, rowStart, null, null);
    }

    public void setPosition(String propertyId, int columnStart, int rowStart) {
        setPosition(propertyId, columnStart, rowStart, null, null);
    }

    public void setPosition(String propertyId, int columnStart, int rowStart, Integer columnEnd,
                            Integer rowEnd) {
        setPosition("", propertyId, columnStart, rowStart, columnEnd, rowEnd);
    }

    public void setPosition(String tabName, String propertyId, int columnStart, int rowStart, Integer columnEnd,
                            Integer rowEnd) {
        FormField formField = (FormField) getField(propertyId);
        formField.setTabName(tabName);
        formField.setColumnStart(columnStart);
        formField.setRowStart(rowStart);
        formField.setColumnEnd(columnEnd);
        formField.setRowEnd(rowEnd);
    }

    public AddRemoveMethodDelegate getTabAddRemoveDelegate(String tabName) {
        return optionalTabs.get(tabName);
    }

    public boolean isTabOptional(String tabName) {
        return optionalTabs.containsKey(tabName);
    }

    public void setTabOptional(String tabName, Object addTarget, String addMethod,
                               Object removeTarget, String removeMethod) {

        MethodDelegate addMethodDelegate = new MethodDelegate(addTarget, addMethod);
        MethodDelegate removeMethodDelegate = new MethodDelegate(removeTarget, removeMethod);
        AddRemoveMethodDelegate addRemoveMethodDelegate = new AddRemoveMethodDelegate(addMethodDelegate,
                removeMethodDelegate);

        optionalTabs.put(tabName, addRemoveMethodDelegate);
    }

    public FormField getFormField(String propertyId) {
        return (FormField) getField(propertyId);
    }

    public void setField(String propertyId, Field field) {
        FormField formField = (FormField) getField(propertyId);
        formField.setField(field);
    }

    public boolean containsPropertyId(String tabName, String propertyId) {
        return containsPropertyId(propertyId) && getFormField(propertyId).getTabName().equals(tabName);
    }

    public Set<FormField> getFormFields(String tabName) {
        Set<FormField> formFields = new HashSet<FormField>();
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            if (formField.getTabName().equals(tabName)) {
                formFields.add(formField);
            }
        }

        return formFields;
    }

    public Set<String> getTabNames() {
        Set<String> tabNames = new LinkedHashSet<String>();
        Collection<DisplayField> displayFields = getFields();
        for (DisplayField displayField : displayFields) {
            FormField formField = (FormField) displayField;
            tabNames.add(formField.getTabName());
        }

        return tabNames;
    }

    public String getLabel(String propertyId) {
        return getField(propertyId).getLabel();
    }

    public void setLabel(String propertyId, String label) {
        getField(propertyId).setLabel(label);
    }

    public void setSelectItems(String propertyId, List items) {
        FormField formField = (FormField) getField(propertyId);
        formField.setSelectItems(items);
    }


    public void addValueChangeListener(String propertyId, Object target, String methodName) {
        FormField formField = (FormField) getField(propertyId);
        formField.addValueChangeListener(target, methodName);
    }

    public boolean attachValidators() {
        return attachValidators;
    }

    public void clearErrors() {
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            if (formField.getField() instanceof AbstractComponent) {
                AbstractComponent fieldComponent = (AbstractComponent) formField.getField();
                fieldComponent.setComponentError(null);
            }
        }
    }

    public static class AddRemoveMethodDelegate {
        private MethodDelegate addMethodDelegate;
        private MethodDelegate removeMethodDelegate;

        private AddRemoveMethodDelegate(MethodDelegate addMethodDelegate, MethodDelegate removeMethodDelegate) {
            this.addMethodDelegate = addMethodDelegate;
            this.removeMethodDelegate = removeMethodDelegate;
        }

        public MethodDelegate getAddMethodDelegate() {
            return addMethodDelegate;
        }

        public MethodDelegate getRemoveMethodDelegate() {
            return removeMethodDelegate;
        }
    }
}
