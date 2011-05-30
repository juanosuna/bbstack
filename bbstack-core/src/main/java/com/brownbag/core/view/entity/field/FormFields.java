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

import com.brownbag.core.util.MessageSource;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;
import org.hibernate.type.AbstractComponentType;

import java.util.Collection;
import java.util.List;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:15 PM
 */
public class FormFields extends DisplayFields {

    private boolean attachValidators = false;

    public FormFields(Class type, MessageSource messageSource, boolean attachValidators) {
        super(type, messageSource);
        this.attachValidators = attachValidators;
    }

    public int getColumns() {
        int columns = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            columns = Math.max(columns, formField.getColumnStart());
            if (formField.getColumnEnd() != null) {
                columns = Math.max(columns, formField.getColumnEnd());
            }
        }

        return ++columns;
    }

    public int getRows() {
        int rows = 0;
        Collection<DisplayField> fields = getFields();
        for (DisplayField field : fields) {
            FormField formField = (FormField) field;
            rows = Math.max(rows, formField.getRowStart());
            if (formField.getRowEnd() != null) {
                rows = Math.max(rows, formField.getRowEnd());
            }
        }

        return ++rows;
    }

    @Override
    protected DisplayField createField(String propertyId) {
        return new FormField(this, propertyId);
    }

    public void setPosition(String propertyId, int columnStart, int rowStart) {
        setPosition(propertyId, columnStart, rowStart, null, null);
    }

    public void setPosition(String propertyId, int columnStart, int rowStart, Integer columnEnd,
                            Integer rowEnd) {
        FormField formField = (FormField) getField(propertyId);
        formField.setColumnStart(columnStart);
        formField.setRowStart(rowStart);
        formField.setColumnEnd(columnEnd);
        formField.setRowEnd(rowEnd);
    }

    public FormField getFormField(String propertyId) {
        return (FormField) getField(propertyId);
    }

    public void setField(String propertyId, Field field) {
        FormField formField = (FormField) getField(propertyId);
        formField.setField(field);
    }

    public void addValueChangeListener(String propertyId, Object target, String methodName) {
        FormField formField = (FormField) getField(propertyId);
        formField.addValueChangeListener(target, methodName);
    }

    public boolean isAttachValidators() {
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
}
