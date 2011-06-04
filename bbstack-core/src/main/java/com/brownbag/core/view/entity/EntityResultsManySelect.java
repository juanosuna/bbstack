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
import com.brownbag.core.view.MainApplication;
import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResultsManySelect<T> extends EntityResultsComponent {

    private Window popupWindow;

    private Button addButton;

    private ContextMenu contextMenu;

    private EntityDao entityDao;

    protected EntityResultsManySelect() {
        super();
    }

    public abstract EntitySelect getEntitySelect();

    public abstract String getPropertyId();

    public EntityDao getEntityDao() {
        return entityDao;
    }

    void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public void postConstruct() {
        super.postConstruct();

        addButton = new Button(getUiMessageSource().getMessage("entityResults.add"), this, "add");
        addButton.addStyleName("small default");
        getButtonPanel().addComponent(addButton);

        addSelectionChangedListener(this, "selectionChanged");
        contextMenu = new ContextMenu();
        getEntityTable().setMultiSelect(true);
        getEntitySelect().getEntityResults().getEntityTable().setMultiSelect(true);
    }

    public void add() {
        popupWindow = new Window("Select Entity");
        popupWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        popupWindow.setModal(true);
        EntitySelect entitySelect = getEntitySelect();
        entitySelect.getEntityResults().getEntityQuery().clear();
        entitySelect.getEntityResults().search();
        popupWindow.addComponent(entitySelect);
        popupWindow.setClosable(true);
        getEntitySelect().getEntityResults().addSelectButtonListener(this, "itemsSelected");
        MainApplication.getInstance().getMainWindow().addWindow(popupWindow);
    }

    public void itemsSelected() {
        close();
        Collection selectedValues = getEntitySelect().getEntityResults().getSelectedValues();
        valuesSelected(selectedValues.toArray());
    }

    @Override
    public EntityManySelectQuery getEntityQuery() {
        return (EntityManySelectQuery) super.getEntityQuery();
    }

    public void valuesSelected(Object... values) {
        Object parent = getEntityQuery().getParent();
        for (Object value : values) {
            value = entityDao.getReference(value);
            try {
                BeanUtils.setProperty(value, getPropertyId(), parent);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            entityDao.persist(value);
        }
        search();
    }

    public void valuesRemoved(Object... values) {
        for (Object value : values) {
            value = entityDao.getReference(value);
            try {
                BeanUtils.setProperty(value, getPropertyId(), null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            entityDao.persist(value);
        }
        search();
    }

    public void setAddButtonEnabled(boolean isEnabled) {
        addButton.setEnabled(isEnabled);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(popupWindow);
    }

    public void remove() {
        Collection selectedValues = getSelectedValues();
        valuesRemoved(selectedValues.toArray());
    }

    public void selectionChanged(Property.ValueChangeEvent event) {
        Collection itemIds = (Collection) getEntityTable().getValue();
        if (itemIds.size() > 0) {
            getEntityTable().addActionHandler(contextMenu);
        } else {
            getEntityTable().removeActionHandler(contextMenu);
        }
    }

    public class ContextMenu implements Action.Handler {
        private Action removeAction = new Action(getUiMessageSource().getMessage("entityResults.remove"));
        private Action[] allActions = new Action[]{removeAction};

        @Override
        public Action[] getActions(Object target, Object sender) {
            return allActions;
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            if (removeAction == action) {
                remove();
            }
        }
    }
}
