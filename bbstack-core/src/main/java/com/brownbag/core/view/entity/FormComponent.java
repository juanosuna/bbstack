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
import com.brownbag.core.view.entity.util.LayoutContextMenu;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NullCapableBeanItem;
import com.vaadin.data.util.NullCapableNestedPropertyDescriptor;
import com.vaadin.data.util.VaadinPropertyDescriptor;
import com.vaadin.ui.*;
import org.vaadin.peter.contextmenu.ContextMenu;

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
    private Map<String, Integer> tabPositions = new HashMap<String, Integer>();
    private LayoutContextMenu menu;

    public abstract String getEntityCaption();

    public abstract void configureFields(FormFields formFields);

    abstract void createFooterButtons(HorizontalLayout footerButtons);

    abstract FormFields createFormFields();

    public MessageSource getUiMessageSource() {
        return uiMessageSource;
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public Form getForm() {
        return form;
    }

    public FormFields getFormFields() {
        return formFields;
    }

    public ResultsComponent getResults() {
        return results;
    }

    public void setResults(ResultsComponent results) {
        this.results = results;
    }

    public void postConstruct() {
        setSizeUndefined();
        form = new ConfigurableForm();
        form.setSizeUndefined();

        form.setWriteThrough(false);
        form.setInvalidCommitted(true);
        form.setImmediate(true);
        form.setValidationVisibleOnCommit(true);
        form.addStyleName("entityForm");

        formFields = createFormFields();
        configureFields(formFields);
        form.setFormFieldFactory(new EntityFieldFactory(formFields));

        final GridLayout gridLayout = formFields.createGridLayout();
        form.setLayout(gridLayout);

        createFooterButtons((HorizontalLayout) form.getFooter());
        form.setCaption(getEntityCaption());

        VerticalLayout layout = new VerticalLayout();
        if (formFields.getTabNames().size() > 1) {
            initializeTabs(layout);
        }

        layout.addComponent(form);
        setCompositionRoot(layout);
        setCustomSizeUndefined();
    }

    private void initializeTabs(VerticalLayout layout) {
        final Set<String> tabNames = formFields.getTabNames();

        tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        menu = new LayoutContextMenu(layout);
        int tabPosition = 0;
        boolean hasOptionalTabs = false;
        for (String tabName : tabNames) {
            TabSheet.Tab tab = tabSheet.addTab(new Label(), tabName, null);
            tabPositions.put(tabName, tabPosition++);
            if (formFields.isTabOptional(tabName)) {
                menu.addAction(uiMessageSource.getMessage("formComponent.add") + " " + tabName,
                        this, "executeContextAction").setVisible(true);
                menu.addAction(uiMessageSource.getMessage("formComponent.remove") + " " + tabName,
                        this, "executeContextAction").setVisible(false);
                setIsRequiredEnable(tabName, false);
                tab.setVisible(false);
                hasOptionalTabs = true;
            }
        }

        if (hasOptionalTabs) {
            for (String tabName : tabNames) {
                TabSheet.Tab tab = getTabByName(tabName);
                tab.setDescription(uiMessageSource.getMessage("formComponent.tab.description"));
            }
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

    protected void resetTabs() {
        if (tabSheet == null) return;

        Set<String> tabNames = formFields.getTabNames();
        for (String tabName : tabNames) {
            if (formFields.isTabOptional(tabName)) {
                Set<FormField> fields = formFields.getFormFields(tabName);
                boolean isTabEmpty = true;
                for (FormField field : fields) {
                    if (field.getField().getValue() != null) {
                        isTabEmpty = false;
                        break;
                    }
                }
                setIsRequiredEnable(tabName, !isTabEmpty);
                TabSheet.Tab tab = getTabByName(tabName);
                tab.setVisible(!isTabEmpty);
            }
        }

        resetContextMenu();
        selectFirstTab();
    }

    private void resetContextMenu() {
        Set<String> tabNames = formFields.getTabNames();
        for (String tabName : tabNames) {
            TabSheet.Tab tab = getTabByName(tabName);

            String caption = uiMessageSource.getMessage("formComponent.add") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(!tab.isVisible());
            }
            caption = uiMessageSource.getMessage("formComponent.remove") + " " + tabName;
            if (menu.containsItem(caption)) {
                menu.getContextMenuItem(caption).setVisible(tab.isVisible());
            }
        }
    }

    public void executeContextAction(ContextMenu.ContextMenuItem item) {
        String name = item.getName();

        if (name.startsWith(uiMessageSource.getMessage("formComponent.add") + " ")) {
            String tabName = name.substring(4);
            FormFields.AddRemoveMethodDelegate addRemoveMethodDelegate = formFields.getTabAddRemoveDelegate(tabName);
            addRemoveMethodDelegate.getAddMethodDelegate().execute();
            TabSheet.Tab tab = getTabByName(tabName);
            setIsRequiredEnable(tabName, true);
            tab.setVisible(true);
            tabSheet.setSelectedTab(tab.getComponent());
        } else if (name.startsWith(uiMessageSource.getMessage("formComponent.remove") + " ")) {
            String tabName = name.substring(7);
            FormFields.AddRemoveMethodDelegate addRemoveMethodDelegate = formFields.getTabAddRemoveDelegate(tabName);
            addRemoveMethodDelegate.getRemoveMethodDelegate().execute();
            TabSheet.Tab tab = getTabByName(tabName);
            setIsRequiredEnable(tabName, false);
            tab.setVisible(false);
        }
        resetContextMenu();
    }

    private void setIsRequiredEnable(String tabName, boolean isEnabled) {
        Set<FormField> fields = formFields.getFormFields(tabName);
        for (FormField field : fields) {
            if (isEnabled) {
                field.restoreIsRequired();
            } else {
                field.disableIsRequired();
            }
        }
    }

    public TabSheet.Tab getTabByName(String tabName) {
        Integer position = tabPositions.get(tabName);
        return tabSheet.getTab(position);
    }

    public String getCurrentTabName() {
        if (tabSheet == null || tabSheet.getSelectedTab() == null) {
            return formFields.getFirstTabName();
        } else {
            return tabSheet.getTab(tabSheet.getSelectedTab()).getCaption();
        }
    }

    public void selectFirstTab() {
        if (tabSheet != null) {
            tabSheet.setSelectedTab(tabSheet.getTab(0).getComponent());
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
