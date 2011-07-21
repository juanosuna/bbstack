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
import com.brownbag.core.entity.WritableEntity;
import com.brownbag.core.util.MethodDelegate;
import com.brownbag.core.util.SpringApplicationContext;
import com.brownbag.core.validation.AssertTrueForProperty;
import com.brownbag.core.validation.Validation;
import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.entity.field.FormField;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityForm<T> extends FormComponent<T> {

    @Resource
    private Validation validation;

    private Window formWindow;
    private TabSheet toManyRelationshipTabs;

    private Button nextButton;
    private Button previousButton;

    private Button saveButton;

    public abstract void configurePopupWindow(Window popupWindow);

    private MethodDelegate closeListener;

    public List<ToManyRelationship> getToManyRelationships() {
        return new ArrayList<ToManyRelationship>();
    }

    FormFields createFormFields() {
        return new FormFields(getEntityType(), entityMessageSource, true);
    }

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();


        List<ToManyRelationship> toManyRelationships = getToManyRelationships();
        if (toManyRelationships.size() > 0) {
            toManyRelationshipTabs = new TabSheet();
            toManyRelationshipTabs.setSizeUndefined();
            for (ToManyRelationship toManyRelationship : toManyRelationships) {
                toManyRelationshipTabs.addTab(toManyRelationship);
            }
            addComponent(toManyRelationshipTabs);
        }
    }

    @Override
    protected Component animate(Component component) {
        if (getToManyRelationships().size() > 0) {
            return super.animate(component);
        } else {
            return component;
        }
    }

    @Override
    protected void createFooterButtons(HorizontalLayout footerLayout) {
        footerLayout.setSpacing(true);
        footerLayout.setMargin(true);

        Button cancelButton = new Button(uiMessageSource.getMessage("entityForm.cancel"), this, "cancel");
        cancelButton.setDescription(uiMessageSource.getMessage("entityForm.cancel.description"));
        cancelButton.setIcon(new ThemeResource("icons/16/cancel.png"));
        cancelButton.addStyleName("small default");
        footerLayout.addComponent(cancelButton);

        Button resetButton = new Button(uiMessageSource.getMessage("entityForm.reset"), this, "reset");
        resetButton.setDescription(uiMessageSource.getMessage("entityForm.reset.description"));
        resetButton.setIcon(new ThemeResource("icons/16/refresh.png"));
        resetButton.addStyleName("small default");
        footerLayout.addComponent(resetButton);

        saveButton = new Button(uiMessageSource.getMessage("entityForm.save"), this, "save");
        saveButton.setDescription(uiMessageSource.getMessage("entityForm.save.description"));
        saveButton.setIcon(new ThemeResource("icons/16/save.png"));
        saveButton.addStyleName("small default");
        footerLayout.addComponent(saveButton);
    }

    public void load(WritableEntity entity) {
        load(entity, true);
    }

    public void load(WritableEntity entity, boolean selectFirstTab) {
        clearComponentErrors();

        WritableEntity loadedEntity = (WritableEntity) getEntityDao().find(entity.getId());
        BeanItem beanItem = createBeanItem(loadedEntity);
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());

        loadToManyRelationships();
        resetTabs(selectFirstTab);
    }

    private EntityDao getEntityDao() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, getEntityType());
    }

    public void loadToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = getToManyRelationships();
        if (toManyRelationships.size() > 0) {
            for (ToManyRelationship toManyRelationship : toManyRelationships) {
                Object parent = getEntity();
                toManyRelationship.getResultsComponent().getEntityQuery().clear();
                toManyRelationship.getResultsComponent().getEntityQuery().setParent(parent);
                toManyRelationship.getResultsComponent().search();

            }
            toManyRelationshipTabs.setVisible(true);
        }
    }

    public void clear() {
        clearComponentErrors();
        getForm().setItemDataSource(null, getFormFields().getPropertyIds());
    }

    public void create() {
        createImpl();
        open(false);

        if (getToManyRelationships().size() > 0) {
            toManyRelationshipTabs.setVisible(false);
        }
    }

    private void createImpl() {
        clearComponentErrors();
        Object newEntity = createEntity();
        BeanItem beanItem = createBeanItem(newEntity);
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
        clearComponentErrors();

        resetTabs();
    }

    private T createEntity() {
        try {
            return (T) getEntityType().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void open(boolean createNavigationButtons) {
        formWindow = new Window(getEntityCaption());
        formWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) formWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        formWindow.setSizeUndefined();
        formWindow.setModal(true);
        formWindow.setClosable(true);
        formWindow.setScrollable(true);

        formWindow.addComponent(createNavigationFormLayout(createNavigationButtons));

        configurePopupWindow(formWindow);
        MainApplication.getInstance().getMainWindow().addWindow(formWindow);
    }

    private HorizontalLayout createNavigationFormLayout(boolean createNavigationButtons) {
        HorizontalLayout navigationFormLayout = new HorizontalLayout();
        navigationFormLayout.setSizeUndefined();

        if (createNavigationButtons) {
            VerticalLayout previousButtonLayout = new VerticalLayout();
            previousButtonLayout.setSizeUndefined();
            previousButtonLayout.setMargin(false);
            previousButtonLayout.setSpacing(false);
            Label spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
            spaceLabel.setSizeUndefined();
            previousButtonLayout.addComponent(spaceLabel);

            previousButton = new Button(null, this, "previousItem");
            previousButton.setDescription(uiMessageSource.getMessage("entityForm.previous.description"));
            previousButton.setSizeUndefined();
            previousButton.addStyleName("borderless");
            previousButton.setIcon(new ThemeResource("icons/16/previous.png"));
            previousButtonLayout.addComponent(previousButton);
            navigationFormLayout.addComponent(previousButtonLayout);
            navigationFormLayout.setComponentAlignment(previousButtonLayout, Alignment.TOP_LEFT);
        }

        navigationFormLayout.addComponent(this);

        if (createNavigationButtons) {
            VerticalLayout nextButtonLayout = new VerticalLayout();
            nextButtonLayout.setSizeUndefined();
            nextButtonLayout.setMargin(false);
            nextButtonLayout.setSpacing(false);
            Label spaceLabel = new Label("</br></br></br>", Label.CONTENT_XHTML);
            spaceLabel.setSizeUndefined();
            nextButtonLayout.addComponent(spaceLabel);

            nextButton = new Button(null, this, "nextItem");
            nextButton.setDescription(uiMessageSource.getMessage("entityForm.next.description"));
            nextButton.setSizeUndefined();
            nextButton.addStyleName("borderless");
            nextButton.setIcon(new ThemeResource("icons/16/next.png"));

            nextButtonLayout.addComponent(nextButton);
            navigationFormLayout.addComponent(nextButtonLayout);
            navigationFormLayout.setComponentAlignment(nextButtonLayout, Alignment.TOP_RIGHT);

            navigationFormLayout.setSpacing(false);
            navigationFormLayout.setMargin(false);

            refreshNavigationButtonStates();
        }

        return navigationFormLayout;
    }

    void refreshNavigationButtonStates() {
        if (getEntityDao().isPersistent(getEntity())) {
            previousButton.setEnabled(((Results) getResults()).hasPreviousItem());
            nextButton.setEnabled(((Results) getResults()).hasNextItem());
        } else {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    public void previousItem() {
        ((Results) getResults()).editPreviousItem();
        refreshNavigationButtonStates();
    }

    public void nextItem() {
        ((Results) getResults()).editNextItem();
        refreshNavigationButtonStates();
    }

    public void close() {
        if (getResults() != null) {
            getResults().search();
        }

        MainApplication.getInstance().getMainWindow().removeWindow(formWindow);
        formWindow = null;

        if (closeListener != null) {
            closeListener.execute();
        }
    }

    public void setCloseListener(Object target, String methodName) {
        closeListener = new MethodDelegate(target, methodName);
    }

    public void cancel() {
        clearComponentErrors();
        getForm().discard();
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        if (beanItem == null) {
            clear();
        } else {
            WritableEntity entity = (WritableEntity) beanItem.getBean();
            if (entity.getId() == null) {
                clear();
            } else {
                load(entity);
            }
        }

        close();
    }

    public void save() {
        getForm().commit();
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        WritableEntity entity = (WritableEntity) beanItem.getBean();

        boolean isValid = validate(entity);
        if (getForm().isValid() && isValid) {
            if (entity.getId() != null) {
                entity.updateLastModified();
                WritableEntity mergedEntity = (WritableEntity) getEntityDao().merge(entity);
                load(mergedEntity);
            } else {
                getEntityDao().persist(entity);
                load(entity);
            }
            close();
        }
    }

    private boolean validate(WritableEntity entity) {
        clearComponentErrors();

        Set<ConstraintViolation<WritableEntity>> constraintViolations = validation.validate(entity);
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();

            ConstraintDescriptor descriptor = constraintViolation.getConstraintDescriptor();
            Annotation annotation = descriptor.getAnnotation();

            FormField field;
            if (annotation instanceof AssertTrueForProperty) {
                if (propertyPath.lastIndexOf(".") > 0) {
                    propertyPath = propertyPath.substring(0, propertyPath.lastIndexOf(".") + 1);
                } else {
                    propertyPath = "";
                }
                AssertTrueForProperty assertTrueForProperty = (AssertTrueForProperty) annotation;
                propertyPath += assertTrueForProperty.property();
                field = getFormFields().getFormField(propertyPath);
                AbstractComponent fieldComponent = (AbstractComponent) field.getField();
                UserError userError = new UserError(constraintViolation.getMessage());
                fieldComponent.setComponentError(userError);
            } else {
                field = getFormFields().getFormField(propertyPath);
            }

            String tabName = field.getTabName();
            TabSheet.Tab tab = getTabByName(tabName);
            tab.setComponentError(new UserError("Tab contains invalid values"));
        }

        if (!constraintViolations.isEmpty()) {
            saveButton.setComponentError(new UserError("Form contains invalid values"));
        }

        return constraintViolations.isEmpty();
    }

    public void clearComponentErrors() {
        getFormFields().clearErrors();
        getForm().setComponentError(null);
        saveButton.setComponentError(null);

        Set<String> tabNames = getFormFields().getTabNames();
        for (String tabName : tabNames) {
            getTabByName(tabName).setComponentError(null);
        }
    }

    public void reset() {
        clearComponentErrors();
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        if (beanItem == null) {
            createImpl();
        } else {
            WritableEntity entity = (WritableEntity) beanItem.getBean();
            if (entity.getId() == null) {
                createImpl();
            } else {
                getForm().discard();
                load(entity, false);
            }
        }
    }
}
