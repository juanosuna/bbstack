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

import com.brownbag.core.dao.EntityDao;
import com.brownbag.core.entity.ReferenceEntity;
import com.brownbag.core.util.BeanProperty;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

import java.util.Date;
import java.util.List;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:10 PM
 */
public class FormField extends DisplayField {
    private Field field;
    private Integer columnStart;
    private Integer rowStart;
    private Integer columnEnd;
    private Integer rowEnd;

    public FormField(FormFields formFields, String propertyId) {
        super(formFields, propertyId);
    }

    public Integer getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(Integer columnStart) {
        this.columnStart = columnStart;
    }

    public Integer getRowStart() {
        return rowStart;
    }

    public void setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
    }

    public Integer getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(Integer columnEnd) {
        this.columnEnd = columnEnd;
    }

    public Integer getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(Integer rowEnd) {
        this.rowEnd = rowEnd;
    }

    public Field getField() {
        if (field == null) {
            field = generateField();
            initializeFieldDefaults();
        }

        return field;
    }

    public void setField(Field field) {
        setField(field, true);
    }

    public void setField(Field field, boolean initializeDefaults) {
        this.field = field;
        if (initializeDefaults) {
            initializeFieldDefaults();
        }
    }

    public void addValueChangeListener(Object target, String methodName) {
        AbstractComponent component = (AbstractComponent) getField();
        component.addListener(Field.ValueChangeEvent.class, target, methodName);
    }

    public FormFields getFormFields() {
        return (FormFields) getDisplayFields();
    }

    private Field generateField() {
        Class propertyType = getPropertyType();

        if (propertyType == null) {
            return null;
        }

        if (Date.class.isAssignableFrom(propertyType)) {
            return new DateField();
        }

        if (Boolean.class.isAssignableFrom(propertyType)) {
            return new CheckBox();
        }

        if (ReferenceEntity.class.isAssignableFrom(propertyType)) {
            return new Select();
        }

        return new TextField();
    }

    private void initializeFieldDefaults() {
        if (field == null) {
            return;
        }

        field.setCaption(getLabel());
        field.setInvalidAllowed(true);

        if (getFormFields().isAttachValidators()) {
            BeanProperty beanProperty = ReflectionUtil.getBeanProperty(getDisplayFields().getEntityType(),
                    getPropertyId());
            if (beanProperty.isValidationOn()) {
                BeanValidationValidator.addValidator(field, beanProperty.getId(), beanProperty.getContainerType());
            }
        }

        if (field instanceof AbstractField) {
            initAbstractFieldDefaults((AbstractField) field);
        }

        if (field instanceof TextField) {
            initTextFieldDefaults((TextField) field);
        }

        if (field instanceof DateField) {
            initDateFieldDefaults((DateField) field);
        }

        if (field instanceof Select) {
            EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class,
                    getPropertyType());
            List referenceEntities = propertyDao.findAll();
            initComboBoxDefaults((Select) field, getPropertyType(), referenceEntities);
        }
    }

    public static void initAbstractFieldDefaults(AbstractField field) {
        field.setRequiredError("Required value is missing");
        field.setImmediate(true);
        field.setInvalidCommitted(true);
    }

    public static void initTextFieldDefaults(TextField field) {
        field.setNullRepresentation("");
        field.setNullSettingAllowed(false);
    }

    public static void initDateFieldDefaults(DateField field) {
        field.setResolution(DateField.RESOLUTION_DAY);
    }

    public static void initComboBoxDefaults(Select field, Class propertyType, List referenceEntities) {
        BeanItemContainer container = new BeanItemContainer(propertyType, referenceEntities);

        field.setContainerDataSource(container);
        field.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
        field.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        field.setNullSelectionAllowed(true);
        field.setItemCaptionPropertyId("name"); // todo can't hard-code
        field.setImmediate(true);
    }
}
