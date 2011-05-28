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
import com.brownbag.core.validation.Validation;
import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.entity.field.FormFields;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityForm<T> extends FormComponent<T> {

    private Validation validation;
    private EntityDao entityDao;

    private Window formWindow;

    Validation getValidation() {
        return validation;
    }

    void setValidation(Validation validation) {
        this.validation = validation;
    }

    EntityDao getEntityDao() {
        return entityDao;
    }

    void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    FormFields createFormFields() {
        return new FormFields(getEntityType(), getEntityMessageSource(), true);
    }

    protected HorizontalLayout createFooterButtons() {
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setSpacing(true);

        Button cancelButton = new Button(getUiMessageSource().getMessage("entityForm.cancel"), this, "cancel");
        footerLayout.addComponent(cancelButton);

        Button resetButton = new Button(getUiMessageSource().getMessage("entityForm.reset"), this, "reset");
        footerLayout.addComponent(resetButton);

        Button saveButton = new Button(getUiMessageSource().getMessage("entityForm.save"), this, "save");
        footerLayout.addComponent(saveButton);

        return footerLayout;
    }

    public void load(WritableEntity entity) {
        getForm().setComponentError(null);
        WritableEntity loadedEntity = (WritableEntity) getEntityDao().find(entity.getId());
        BeanItem beanItem = createBeanItem(loadedEntity);
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
    }

    public void clear() {
        getForm().setComponentError(null);
        getForm().setItemDataSource(null, getFormFields().getPropertyIds());
    }

    public void create() {
        createImpl();
        open();
    }

    private void createImpl() {
        getForm().setComponentError(null);
        Object newEntity = createEntity();
        BeanItem beanItem = createBeanItem(newEntity);
        getForm().setItemDataSource(beanItem, getFormFields().getPropertyIds());
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
        VerticalLayout layout = (VerticalLayout) formWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        formWindow.setModal(true);
        formWindow.addComponent(this);
        formWindow.setClosable(true);
        MainApplication.getInstance().getMainWindow().addWindow(formWindow);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(formWindow);
        formWindow = null;
    }

    public void cancel() {
        getForm().setComponentError(null);
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
        try {
            getForm().commit();
            // hack, not sure why this is happening , since invalid data is allowed
        } catch (com.vaadin.data.Validator.InvalidValueException e) {
        }
        BeanItem beanItem = (BeanItem) getForm().getItemDataSource();
        WritableEntity entity = (WritableEntity) beanItem.getBean();

        Set<ConstraintViolation> constraintViolations = validate(entity);
        if (getForm().isValid() && constraintViolations.isEmpty()) {
            if (entity.getId() != null) {
                entity.updateLastModified();
                WritableEntity mergedEntity = (WritableEntity) getEntityDao().merge(entity);
                load(mergedEntity);
                getEntityResults().search();
            } else {
                getEntityDao().persist(entity);
                load(entity);
                getEntityResults().search();
            }
            close();
        }
    }

    private Set<ConstraintViolation> validate(WritableEntity entity) {
        Set<ConstraintViolation> constraintViolations = (Set<ConstraintViolation>) validation.validate(entity);
        if (!constraintViolations.isEmpty()) {
            List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
            for (ConstraintViolation constraintViolation : constraintViolations) {
                String message = constraintViolation.getMessage();
                errorMessages.add(new UserError(message));
            }
            CompositeErrorMessage compositeErrorMessage = new CompositeErrorMessage(errorMessages);
            getForm().setComponentError(compositeErrorMessage);
        } else {
            getForm().setComponentError(null);
        }

        return constraintViolations;
    }

    public void reset() {
        getForm().setComponentError(null);
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
