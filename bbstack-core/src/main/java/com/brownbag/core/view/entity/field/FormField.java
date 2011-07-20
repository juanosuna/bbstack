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
import com.brownbag.core.util.BeanPropertyType;
import com.brownbag.core.util.CurrencyUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.brownbag.core.util.assertion.Assert;
import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import sun.jdbc.odbc.ee.ObjectPool;

import javax.persistence.Lob;
import java.util.*;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:10 PM
 */
public class FormField extends DisplayField {
    public static final String DEFAULT_DISPLAY_PROPERTY_ID = "name";

    private String tabName = "";
    private Field field;
    private Integer columnStart;
    private Integer rowStart;
    private Integer columnEnd;
    private Integer rowEnd;
    private boolean isRequired;

    public FormField(FormFields formFields, String propertyId) {
        super(formFields, propertyId);
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
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

    public void setSelectItems(List items) {
        // could be either collection or single item
        Object selectedItems = getSelectedItems();

        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        AbstractSelect selectField = (AbstractSelect) field;
        if (selectField.getContainerDataSource() == null
                || !(selectField.getContainerDataSource() instanceof BeanItemContainer)) {
            BeanItemContainer container;
            if (isCollectionType()) {
                container = new BeanItemContainer(getCollectionValueType(), items);
            } else {
                container = new BeanItemContainer(getPropertyType(), items);
            }

            selectField.setContainerDataSource(container);
        } else {
            BeanItemContainer container = (BeanItemContainer) selectField.getContainerDataSource();
            container.removeAllItems();
            container.addAll(items);

            if (!isCollectionType() && !container.containsId(selectedItems)) {
                selectField.select(selectField.getNullSelectionItemId());
            }
        }
    }

    public void setMultiSelectDimensions(int rows, int columns) {
        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof ListSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        ListSelect selectField = (ListSelect) field;
        selectField.setRows(rows);
        selectField.setColumns(columns);
    }

    public void setDisplayPropertyId(String displayPropertyId) {
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a Select field");

        ((AbstractSelect) field).setItemCaptionPropertyId(displayPropertyId);
    }

    public Object getSelectedItems() {
        Field field = getField();
        Assert.PROGRAMMING.assertTrue(field instanceof AbstractSelect,
                "property " + getPropertyId() + " is not a AbstractSelect field");
        AbstractSelect selectField = (AbstractSelect) field;
        return selectField.getValue();
    }

    public void addValueChangeListener(Object target, String methodName) {
        AbstractComponent component = (AbstractComponent) getField();
        component.addListener(Property.ValueChangeEvent.class, target, methodName);
    }

    public FormFields getFormFields() {
        return (FormFields) getDisplayFields();
    }

    public void setVisible(boolean isVisible) {
        getField().setVisible(isVisible);
    }

    public void setRequired(boolean isRequired) {
        getField().setRequired(isRequired);
    }

    public void disableIsRequired() {
        getField().setRequired(false);
    }

    public void restoreIsRequired() {
        getField().setRequired(isRequired);
    }

    private Field generateField() {
        Class propertyType = getPropertyType();

        if (propertyType == null) {
            return null;
        }

        if (Date.class.isAssignableFrom(propertyType)) {
            return new DateField();
        }

        if (boolean.class.isAssignableFrom(propertyType) || Boolean.class.isAssignableFrom(propertyType)) {
            return new CheckBox();
        }

        if (ReferenceEntity.class.isAssignableFrom(propertyType)) {
            return new Select();
        }

        if (Currency.class.isAssignableFrom(propertyType)) {
            return new Select();
        }

        if (propertyType.isEnum()) {
            return new Select();
        }

        if (Collection.class.isAssignableFrom(propertyType)) {
            return new ListSelect();
        }

        if (hasAnnotation(Lob.class)) {
            return new RichTextArea();
        }

        return new TextField();
    }

    private void initializeFieldDefaults() {
        if (field == null) {
            return;
        }

        field.setCaption(getLabel());
        field.setInvalidAllowed(true);

        if (getFormFields().attachValidators()) {
            BeanPropertyType beanProperty = com.brownbag.core.util.BeanPropertyType.getBeanPropertyType(getDisplayFields().getEntityType(),
                    getPropertyId());
            if (beanProperty.isValidatable()) {
                BeanValidationValidator.addValidator(field, beanProperty.getId(), beanProperty.getContainerType());
                isRequired = field.isRequired();
            }
        }

        if (field instanceof AbstractField) {
            initAbstractFieldDefaults((AbstractField) field);
        }

        if (field instanceof TextField) {
            initTextFieldDefaults((TextField) field);
        }

        if (field instanceof RichTextArea) {
            initRichTextFieldDefaults((RichTextArea) field);
        }

        if (field instanceof DateField) {
            initDateFieldDefaults((DateField) field);
        }

        if (field instanceof AbstractSelect) {
            initAbstractSelectDefaults((AbstractSelect) field);

            if (field instanceof Select) {
                initSelectDefaults((Select) field);
            }

            if (field instanceof ListSelect) {
                initListSelectDefaults((ListSelect) field);
            }

            Class valueType = getPropertyType();
            if (isCollectionType()) {
                valueType = getCollectionValueType();
            }

            List referenceEntities;
            if (Currency.class.isAssignableFrom(valueType)) {
                referenceEntities = CurrencyUtil.getAvailableCurrencies();
                ((AbstractSelect) field).setItemCaptionPropertyId("currencyCode");
            } else if (valueType.isEnum()) {
                Object[] enumConstants = valueType.getEnumConstants();
                referenceEntities = Arrays.asList(enumConstants);
            }
            else {
                EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class,
                        valueType);
                referenceEntities = propertyDao.findAll();
            }
            setSelectItems(referenceEntities);
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

    public static void initRichTextFieldDefaults(RichTextArea field) {
        field.setNullRepresentation("");
        field.setNullSettingAllowed(false);
    }

    public static void initDateFieldDefaults(DateField field) {
        field.setResolution(DateField.RESOLUTION_DAY);
    }

    public static void initAbstractSelectDefaults(AbstractSelect field) {
        field.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        field.setNullSelectionAllowed(true);
        field.setItemCaptionPropertyId(DEFAULT_DISPLAY_PROPERTY_ID);
        field.setImmediate(true);
    }

    public static void initSelectDefaults(Select field) {
        field.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
    }

    public static void initListSelectDefaults(ListSelect field) {
        field.setMultiSelect(true);
    }
}
