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
import com.brownbag.core.validation.PatternIfThen;
import com.brownbag.core.validation.Validation;
import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.entity.field.FormField;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.manyselect.ManySelect;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private Validation validation;

    private Window formWindow;
    private TabSheet manySelectTabs;

    private Button nextButton;
    private Button previousButton;

    public List<ManySelect> getManySelects() {
        return new ArrayList<ManySelect>();
    }

    FormFields createFormFields() {
        return new FormFields(getEntityType(), entityMessageSource, true);
    }

    @Override
    public void postConstruct() {
        super.postConstruct();

        List<ManySelect> manySelects = getManySelects();
        if (manySelects.size() > 0) {
            manySelectTabs = new TabSheet();
            manySelectTabs.setSizeUndefined();
            for (ManySelect manySelect : manySelects) {
                manySelectTabs.addTab(manySelect);
            }
            addComponent(manySelectTabs);
        }
    }

    @Override
    protected void createFooterButtons(HorizontalLayout footerLayout) {
        footerLayout.setSpacing(true);
        footerLayout.setMargin(true);

        Button cancelButton = new Button(uiMessageSource.getMessage("entityForm.cancel"), this, "cancel");
        cancelButton.setIcon(new ThemeResource("icons/16/cancel.png"));
        cancelButton.addStyleName("small default");
        footerLayout.addComponent(cancelButton);

        Button resetButton = new Button(uiMessageSource.getMessage("entityForm.reset"), this, "reset");
        resetButton.setIcon(new ThemeResource("icons/16/refresh.png"));
        resetButton.addStyleName("small default");
        footerLayout.addComponent(resetButton);

        Button saveButton = new Button(uiMessageSource.getMessage("entityForm.save"), this, "save");
        saveButton.setIcon(new ThemeResource("icons/16/save.png"));
        saveButton.addStyleName("small default");
        footerLayout.addComponent(saveButton);
    }

    public void load(WritableEntity entity) {
        clearComponentErrors();

        WritableEntity loadedEntity = (WritableEntity) getEntityDao().find(entity.getId());
        BeanItem beanItem = createBeanItem(loadedEntity);
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());

        loadManySelects();
        resetTabs();
    }

    private EntityDao getEntityDao() {
        return getResults().getEntityDao();
    }

    public void loadManySelects() {
        List<ManySelect> manySelects = getManySelects();
        if (manySelects.size() > 0) {
            for (ManySelect manySelect : manySelects) {
                Object parent = getEntity();
                manySelect.getEntityQuery().clear();
                manySelect.getEntityQuery().setParent(parent);
                manySelect.getResultsComponent().search();

            }
            manySelectTabs.setVisible(true);
        }
    }

    public void clear() {
        clearComponentErrors();
        getForm().setItemDataSource(null, getFormFields().getPropertyIds());
    }

    public void create() {
        createImpl();
        open();

        if (getManySelects().size() > 0) {
            manySelectTabs.setVisible(false);
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

    public void open() {
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

        formWindow.addComponent(createNavigationFormLayout());

        MainApplication.getInstance().getMainWindow().addWindow(formWindow);
    }

    private HorizontalLayout createNavigationFormLayout() {
        HorizontalLayout navigationFormLayout = new HorizontalLayout();
        navigationFormLayout.setSizeUndefined();

        previousButton = new Button(null, this, "previousItem");
        previousButton.setSizeUndefined();
        previousButton.addStyleName("borderless");
        previousButton.setIcon(new ThemeResource("icons/16/previous.png"));
        navigationFormLayout.addComponent(previousButton);
        navigationFormLayout.setComponentAlignment(previousButton, Alignment.MIDDLE_LEFT);

        navigationFormLayout.addComponent(this);

        nextButton = new Button(null, this, "nextItem");
        nextButton.setSizeUndefined();
        nextButton.addStyleName("borderless");
        nextButton.setIcon(new ThemeResource("icons/16/next.png"));
        navigationFormLayout.addComponent(nextButton);
        navigationFormLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);
        navigationFormLayout.setSpacing(false);
        navigationFormLayout.setMargin(false);

        refreshNavigationButtonStates();
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
        getResults().search();
        MainApplication.getInstance().getMainWindow().removeWindow(formWindow);
        formWindow = null;
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

        boolean isValid = validatePatternIfThens(entity);
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

    private boolean validatePatternIfThens(WritableEntity entity) {
        boolean isValid = true;
        Set<ConstraintViolation<WritableEntity>> constraintViolations = validation.validate(entity);
        for (ConstraintViolation constraintViolation : constraintViolations) {
            String message = constraintViolation.getMessage();
            UserError error = new UserError(message);
            ConstraintDescriptor descriptor = constraintViolation.getConstraintDescriptor();
            Annotation annotation = descriptor.getAnnotation();
            if (annotation instanceof PatternIfThen) {
                PatternIfThen patternIfThen = (PatternIfThen) annotation;
                String thenProperty = constraintViolation.getPropertyPath()
                        + "." + patternIfThen.thenProperty();
                FormField thenField = getFormFields().getFormField(thenProperty);
                AbstractComponent fieldComponent = (AbstractComponent) thenField.getField();
                fieldComponent.setComponentError(error);
                isValid = false;
            }
        }
        if (isValid) {
            clearComponentErrors();
        }

        return isValid;
    }

    public void clearComponentErrors() {
        getFormFields().clearErrors();
        getForm().setComponentError(null);
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
                load(entity);
            }
        }
    }
}
