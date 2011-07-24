/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
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
import com.brownbag.core.util.StringUtil;
import com.brownbag.core.util.assertion.Assert;
import com.brownbag.core.view.entity.EntityForm;
import com.vaadin.addon.beanvalidation.BeanValidationValidator;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.ui.*;

import javax.persistence.Lob;
import java.util.*;

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

    public boolean isRequired() {
        return getField().isRequired();
    }

    public void restoreIsRequired() {
        getField().setRequired(isRequired);
    }

    public float getWidth() {
        return getField().getWidth();
    }

    public void setWidth(float width, int unit) {
        getField().setWidth(width, unit);
    }

    public String getDescription() {
        return getField().getDescription();
    }

    public void setDescription(String description) {
        getField().setDescription(description);
    }

    public boolean hasError() {
        if (getField() instanceof AbstractComponent) {
            AbstractComponent abstractComponent = (AbstractComponent) getField();
            return abstractComponent.getComponentError() != null || hasIsRequiredError();
        } else {
            return false;
        }
    }

    public boolean hasIsRequiredError() {
        return getField().isRequired() && StringUtil.isEmpty(getField().getValue());
    }

    public void clearError() {
        if (getField() instanceof AbstractComponent) {
            AbstractComponent abstractComponent = (AbstractComponent) getField();
            abstractComponent.setComponentError(null);
        }
    }

    public void addError(ErrorMessage errorMessage) {
        Assert.PROGRAMMING.assertTrue(getField() instanceof AbstractComponent,
                "Error message cannot be added to field that is not an AbstractComponent");

        AbstractComponent abstractComponent = (AbstractComponent) getField();
        ErrorMessage existingErrorMessage = abstractComponent.getComponentError();
        if (existingErrorMessage == null) {
            abstractComponent.setComponentError(errorMessage);
        } else if (existingErrorMessage instanceof CompositeErrorMessage) {
            CompositeErrorMessage existingCompositeErrorMessage = (CompositeErrorMessage) existingErrorMessage;
            Iterator<ErrorMessage> iterator = existingCompositeErrorMessage.iterator();
            Set<ErrorMessage> newErrorMessages = new LinkedHashSet<ErrorMessage>();
            while (iterator.hasNext()) {
                ErrorMessage next = iterator.next();
                newErrorMessages.add(next);
            }
            newErrorMessages.add(errorMessage);
            CompositeErrorMessage newCompositeErrorMessage = new CompositeErrorMessage(newErrorMessages);
            abstractComponent.setComponentError(newCompositeErrorMessage);
        } else {
            Set<ErrorMessage> newErrorMessages = new LinkedHashSet<ErrorMessage>();
            newErrorMessages.add(existingErrorMessage);
            newErrorMessages.add(errorMessage);
            CompositeErrorMessage newCompositeErrorMessage = new CompositeErrorMessage(newErrorMessages);
            abstractComponent.setComponentError(newCompositeErrorMessage);
        }
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
            } else {
                EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class,
                        valueType);
                referenceEntities = propertyDao.findAll();
            }
            setSelectItems(referenceEntities);
        }


        if (getFormFields().isEntityForm()) {
            BeanPropertyType beanPropertyType = com.brownbag.core.util.BeanPropertyType.getBeanPropertyType(getDisplayFields().getEntityType(),
                    getPropertyId());
            if (beanPropertyType.isValidatable()) {
                initializeIsRequired(field, beanPropertyType.getId(), beanPropertyType.getContainerType());
            }

            field.addListener(new FieldValueChangeListener());
        }
    }

    private void initializeIsRequired(Field field, Object propertyId, Class<?> beanClass) {
        BeanValidationValidator validator = new BeanValidationValidator(beanClass, String.valueOf(propertyId));
        if (validator.isRequired()) {
            field.setRequired(true);
            field.setRequiredError(validator.getRequiredMessage());
        }

        isRequired = field.isRequired();
    }

    public class FieldValueChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            EntityForm entityForm = (EntityForm) getFormFields().getForm();
            entityForm.validate();
        }
    }

    public static void initAbstractFieldDefaults(AbstractField field) {
        field.setRequiredError("Required value is missing");
        field.setImmediate(true);
        field.setInvalidCommitted(true);
        field.setWriteThrough(true);
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
