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

import com.brownbag.core.view.MessageSource;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.view.entity.field.FormField;
import com.brownbag.core.view.entity.field.FormFields;
import com.vaadin.data.util.NullCapableBeanItem;
import com.vaadin.data.util.NullCapableNestedPropertyDescriptor;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class FormComponent<T> extends CustomComponent {

    private MessageSource entityMessageSource;
    private MessageSource uiMessageSource;

    private Panel formPanel;
    private Form form;
    private EntityResultsComponent entityResults;
    private FormFields formFields;

    public abstract void configureFormFields(FormFields formFields);

    public abstract String getEntityCaption();

    public Form getForm() {
        return form;
    }

    MessageSource getEntityMessageSource() {
        return entityMessageSource;
    }

    void setEntityMessageSource(MessageSource entityMessageSource) {
        this.entityMessageSource = entityMessageSource;
    }

    MessageSource getUiMessageSource() {
        return uiMessageSource;
    }

    void setUiMessageSource(MessageSource uiMessageSource) {
        this.uiMessageSource = uiMessageSource;
    }

    EntityResultsComponent getEntityResults() {
        return entityResults;
    }

    void setEntityResults(EntityResultsComponent entityResults) {
        this.entityResults = entityResults;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public FormFields getFormFields() {
        return formFields;
    }

    public Panel getFormPanel() {
        return formPanel;
    }

    public void postConstruct() {
        VerticalLayout layout = new VerticalLayout();
        formPanel = EntityComposition.createPanel(layout);

        form = new ConfigurableForm();
        form.setWriteThrough(true);
        form.setInvalidCommitted(true);
        form.setImmediate(true);
        form.setValidationVisibleOnCommit(true);
        form.setStyleName("entityForm");
        formPanel.addComponent(form);

        formFields = createFormFields();
        configureFormFields(formFields);
        form.setFormFieldFactory(new EntityFieldFactory(formFields));

        GridLayout gridLayout = new GridLayout(getFormFields().getColumns(), getFormFields().getRows());
        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);
        gridLayout.setWidth("100%");
        form.setLayout(gridLayout);

        HorizontalLayout footerLayout = createFooterButtons();
        form.getFooter().setMargin(true);
        form.getFooter().addComponent(footerLayout);
        form.setCaption(getEntityCaption());

        setCompositionRoot(formPanel);
    }

    public T getEntity() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        return (T) beanItem.getBean();
    }

    public void refreshFromDataSource() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

    protected abstract HorizontalLayout createFooterButtons();

    abstract FormFields createFormFields();

    protected BeanItem createBeanItem(Object entity) {
        List<String> propertyIds = getFormFields().getPropertyIds();
        Map<String, VaadinPropertyDescriptor> descriptors = new HashMap<String, VaadinPropertyDescriptor>();
        for (String propertyId : propertyIds) {
            VaadinPropertyDescriptor descriptor = new NullCapableNestedPropertyDescriptor(propertyId, getEntityType());
            descriptors.put(propertyId, descriptor);
        }
        return new NullCapableBeanItem(entity, descriptors);
    }

    public static class EntityFieldFactory implements FormFieldFactory {

        private FormFields formFields;

        public EntityFieldFactory(FormFields formFields) {
            this.formFields = formFields;
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            FormField formField = formFields.getFormField(propertyId.toString());

            return formField.getField();
        }
    }

    public class ConfigurableForm extends Form {

        @Override
        protected void attachField(Object propertyId, Field field) {
            GridLayout gridLayout = (GridLayout) form.getLayout();
            FormFields formFields = getFormFields();
            if (formFields.containsPropertyId(propertyId.toString())) {
                Integer columnStart = formFields.getFormField(propertyId.toString()).getColumnStart();
                Integer rowStart = formFields.getFormField(propertyId.toString()).getRowStart();
                Integer columnEnd = formFields.getFormField(propertyId.toString()).getColumnEnd();
                Integer rowEnd = formFields.getFormField(propertyId.toString()).getRowEnd();
                if (columnEnd != null && rowEnd != null) {
                    gridLayout.addComponent(field, columnStart, rowStart, columnEnd, rowEnd);
                } else {
                    gridLayout.addComponent(field, columnStart, rowStart);
                }
            }
        }
    }
}
