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
import com.brownbag.core.entity.WritableEntity;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.brownbag.core.validation.Validation;
import com.vaadin.data.util.POJOItem;
import com.vaadin.terminal.CompositeErrorMessage;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityForm<T> extends Form {

    private EntityDao entityDao;

    private EntityTable entityTable;

    @Autowired
    private Validation validation;

    private GridLayout gridLayout;
    private Window formWindow;

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityDao getEntityDao() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, getEntityType());
    }

    public EntityTable getEntityTable() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityTable.class, getEntityType());
    }

    public T createEntity() {
        try {
            return (T) getEntityType().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void postConstruct() {
        entityDao = getEntityDao();
        entityTable = getEntityTable();

        gridLayout = new GridLayout(getFormConfig().getColumns(), getFormConfig().getRows());
        gridLayout.setMargin(true, false, false, true);
        gridLayout.setSpacing(true);
        setLayout(gridLayout);

        setWriteThrough(true);
        setInvalidCommitted(true);
        setImmediate(true);
        setValidationVisible(true);

        initButtons();
        init();
    }

    public abstract FormConfig getFormConfig();

    public abstract void init();

    private void initButtons() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        Button cancel = new Button("Cancel", this, "cancel");
        buttons.addComponent(cancel);

        Button resetChanges = new Button("Reset", this, "reload");
        buttons.addComponent(resetChanges);

        Button save = new Button("Save", this, "save");
        buttons.addComponent(save);

        getFooter().setMargin(true);
        getFooter().addComponent(buttons);
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        GridLayout gridLayout = (GridLayout) getLayout();
        FormConfig formConfig = getFormConfig();
        if (formConfig.containsProperty(propertyId.toString())) {
            Integer columnStart = formConfig.getFieldConfig(propertyId.toString()).getColumnStart();
            Integer rowStart = formConfig.getFieldConfig(propertyId.toString()).getRowStart();
            Integer columnEnd = formConfig.getFieldConfig(propertyId.toString()).getColumnEnd();
            Integer rowEnd = formConfig.getFieldConfig(propertyId.toString()).getRowEnd();
            if (columnEnd != null && rowEnd != null) {
                gridLayout.addComponent(field, columnStart, rowStart, columnEnd, rowEnd);
            } else {
                gridLayout.addComponent(field, columnStart, rowStart);
            }
        }
    }

    public void load(WritableEntity entity) {
        setComponentError(null);
        WritableEntity loadedEntity = (WritableEntity) entityDao.find(entity.getId());
        POJOItem pojoItem = new POJOItem(loadedEntity, getFormConfig().getPropertyIds());
        super.setItemDataSource(pojoItem, getFormConfig().getPropertyIds());
    }

    public void clear() {
        setComponentError(null);
        super.setItemDataSource(null, getFormConfig().getPropertyIds());
    }

    public void create() {
        setComponentError(null);
        Object newEntity = createEntity();
        POJOItem pojoItem = new POJOItem(newEntity, getFormConfig().getPropertyIds());
        super.setItemDataSource(pojoItem, getFormConfig().getPropertyIds());
        open();
    }

    public void open() {
        formWindow = new Window(getCaption());
        VerticalLayout layout = (VerticalLayout) formWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        formWindow.setModal(true);
        formWindow.addComponent(this);
        formWindow.setClosable(false);
        MainApplication.getInstance().getMainWindow().addWindow(formWindow);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(formWindow);
        formWindow = null;
    }

    public void cancel() {
        reload();
        close();
    }

    public void save() {
        try {
            commit();
            // hack, not sure why this is happening , since invalid data is allowed
        } catch (com.vaadin.data.Validator.InvalidValueException e) {
        }
        POJOItem pojoItem = (POJOItem) getItemDataSource();
        WritableEntity entity = (WritableEntity) pojoItem.getBean();

        Set<ConstraintViolation> constraintViolations = validate(entity);
        if (isValid() && constraintViolations.isEmpty()) {
            if (entity.getId() != null) {
                entity.updateLastModified();
                WritableEntity mergedEntity = (WritableEntity) entityDao.merge(entity);
                load(mergedEntity);
                entityTable.search();
            } else {
                entityDao.persist(entity);
                load(entity);
                entityTable.search();
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
            setComponentError(compositeErrorMessage);
        } else {
            setComponentError(null);
        }

        return constraintViolations;
    }

    public void reload() {
        setComponentError(null);
        discard();
        POJOItem pojoItem = (POJOItem) getItemDataSource();
        WritableEntity entity = (WritableEntity) pojoItem.getBean();
        if (entity.getId() == null) {
            clear();
        } else {
            load(entity);
        }
    }
}
