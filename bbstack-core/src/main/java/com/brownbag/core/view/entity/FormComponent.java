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

import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.view.MessageSource;
import com.brownbag.core.view.entity.field.FormField;
import com.brownbag.core.view.entity.field.FormFields;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NullCapableBeanItem;
import com.vaadin.data.util.NullCapableNestedPropertyDescriptor;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class FormComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource uiMessageSource;

    @Resource
    protected MessageSource entityMessageSource;

    private Form form;
    private ResultsComponent results;
    private FormFields formFields;
    private TabSheet tabSheet;

    public abstract String getEntityCaption();

    public abstract void configureFields(FormFields formFields);

    abstract HorizontalLayout createFooterButtons();

    abstract FormFields createFormFields();

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public Form getForm() {
        return form;
    }

    public FormFields getFormFields() {
        return formFields;
    }

    ResultsComponent getResults() {
        return results;
    }

    void setResults(ResultsComponent results) {
        this.results = results;
    }

    public void postConstruct() {
        form = new ConfigurableForm();
        form.setSizeUndefined();
        form.setWriteThrough(true);
        form.setInvalidCommitted(true);
        form.setImmediate(true);
        form.setValidationVisibleOnCommit(true);
        form.setStyleName("entityForm");

        formFields = createFormFields();
        configureFields(formFields);
        form.setFormFieldFactory(new EntityFieldFactory(formFields));

        final GridLayout gridLayout = formFields.createGridLayout();
        form.setLayout(gridLayout);

        HorizontalLayout footerLayout = createFooterButtons();
        form.getFooter().setMargin(true);
        form.getFooter().addComponent(footerLayout);
        form.setCaption(getEntityCaption());

        VerticalLayout layout = new VerticalLayout();

        final Set<String> tabNames = formFields.getTabNames();
        if (tabNames.size() > 1) {
            tabSheet = new TabSheet();
            tabSheet.setSizeUndefined();
            for (String tabName : tabNames) {
                tabSheet.addTab(new Label(), tabName, null);
            }
            layout.addComponent(tabSheet);

            tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {
                @Override
                public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                    String tabName = getCurrentTabName();
                    GridLayout gridLayout = formFields.createGridLayout(tabName);
                    form.setLayout(gridLayout);
                    refreshFromDataSource();
                }
            });
        }

        layout.addComponent(form);
        setCompositionRoot(layout);
        setCustomSizeUndefined();
    }

    public String getCurrentTabName() {
        if (tabSheet == null) {
            return formFields.getFirstTabName();
        } else {
            return tabSheet.getTab(tabSheet.getSelectedTab()).getCaption();
        }
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    public T getEntity() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        return (T) beanItem.getBean();
    }

    public void refreshFromDataSource() {
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

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
            String currentTabName = getCurrentTabName();
            if (formFields.containsPropertyId(currentTabName, propertyId.toString())) {
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
