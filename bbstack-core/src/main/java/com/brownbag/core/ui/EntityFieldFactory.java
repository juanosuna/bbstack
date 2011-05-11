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

import com.brownbag.core.dao.EntityDao;
import com.brownbag.core.entity.ReferenceEntity;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.brownbag.core.validation.BeanValidationUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/10/11
 * Time: 9:18 PM
 */
public class EntityFieldFactory<T> extends DefaultFieldFactory {
    private FormConfig formConfig;
    private Boolean attachValidators = false;
    private Map<Object, Field> fields = new HashMap<Object, Field>();
    private Map<Object, ValueChangeListener> valueChangeListeners = new HashMap<Object, ValueChangeListener>();

    public EntityFieldFactory(FormConfig formConfig) {
        this.formConfig = formConfig;
    }

    public EntityFieldFactory(FormConfig formConfig, Boolean attachValidators) {
        this.formConfig = formConfig;
        this.attachValidators = attachValidators;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {

        if (!fields.containsKey(propertyId)) {
            Field field = createFieldImpl(item, propertyId, uiContext);
            if (field == null) {
                field = super.createField(item, propertyId, uiContext);
            }
            attachCaption(field, propertyId);
            if (attachValidators) {
                attachValidator(field, propertyId);
            }
            if (field instanceof AbstractTextField) {
                AbstractTextField textField = (AbstractTextField) field;
                textField.setNullRepresentation("");
                textField.setNullSettingAllowed(false);
                textField.setRequiredError("Value is required");
                textField.setImmediate(true);
            }
            if (field instanceof AbstractField) {
                AbstractField abstractField = (AbstractField) field;
                abstractField.setRequiredError("Value is required");
                abstractField.setImmediate(true);
            }
            field.setInvalidAllowed(true);
            fields.put(propertyId, field);
        }

        attachValueChangeListener(propertyId);
        return fields.get(propertyId);
    }

    public void addValueChangeListener(Object propertyId, Object target, String methodName) {
        valueChangeListeners.put(propertyId, new ValueChangeListener(target, methodName));
    }

    private void attachValueChangeListener(Object propertyId) {
        ValueChangeListener valueChangeListener = valueChangeListeners.get(propertyId);
        if (valueChangeListener != null) {
            AbstractComponent field = (AbstractComponent) getField(propertyId);
            field.addListener(Field.ValueChangeEvent.class, valueChangeListener.getTarget(),
                    valueChangeListener.getMethodName());
        }
    }

    protected Field createReferenceComboField(Object propertyId) {
        ReflectionUtil.BeanProperty beanProperty = ReflectionUtil.getType(getEntityType(), propertyId.toString());
        Class propertyType = beanProperty.getType();
        if (propertyType != null && ReferenceEntity.class.isAssignableFrom(propertyType)) {
            EntityDao propertyDao = SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, propertyType);
            List referenceEntities = propertyDao.findAll();
            ComboBox comboBox = createReferenceCombo(propertyId, referenceEntities);
            return comboBox;
        } else {
            return null;
        }
    }

    public Set<Object> getPropertyIds() {
        return fields.keySet();
    }

    public Field getField(Object propertyId) {
        return fields.get(propertyId);
    }

    protected void attachCaption(Field field, Object propertyId) {
        if (formConfig.containsProperty(propertyId.toString())) {
            field.setCaption(formConfig.getLabel(propertyId.toString()));
        }
    }

    protected void attachValidator(Field field, Object propertyId) {
        BeanValidationUtil.addValidator(field, propertyId, getEntityType());
    }

    private Field createFieldImpl(Item item, Object propertyId, Component uiContext) {
        Field field = createCustomField(item, propertyId, uiContext);
        if (field != null) {
            return field;
        } else {
            field = createReferenceComboField(propertyId);
        }

        return field;
    }

    protected Field createCustomField(Item item, Object propertyId, Component uiContext) {
        return null;
    }

    public ComboBox createReferenceCombo(Object propertyId, List referenceEntities) {
        String caption = formConfig.getLabel(propertyId.toString());
        ComboBox comboBox = new ComboBox(caption);

        ReflectionUtil.BeanProperty beanProperty = ReflectionUtil.getType(getEntityType(), propertyId.toString());
        Class clazz = beanProperty.getType();
        BeanItemContainer container = new BeanItemContainer(clazz);
        container.addAll(referenceEntities);

        comboBox.setContainerDataSource(container);
        comboBox.setFilteringMode(ComboBox.FILTERINGMODE_OFF);
        comboBox.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
        comboBox.setNullSelectionAllowed(true);
        comboBox.setItemCaptionPropertyId("name");
        comboBox.setImmediate(true);

        return comboBox;
    }

    public static class ValueChangeListener {
        private Object target;
        private String methodName;

        public ValueChangeListener(Object target, String methodName) {
            this.target = target;
            this.methodName = methodName;
        }

        public Object getTarget() {
            return target;
        }

        public String getMethodName() {
            return methodName;
        }
    }
}
