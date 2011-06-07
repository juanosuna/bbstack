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

package com.brownbag.core.view.entity.manyselect;

import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.MessageSource;
import com.brownbag.core.view.entity.EntityResultsComponent;
import com.brownbag.core.view.entity.singleselect.EntitySingleSelect;
import com.brownbag.core.view.entity.util.ContextMenu;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityManySelectResults<T> extends EntityResultsComponent<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource(name = "entityMessageSource")
    private MessageSource entityMessageSource;

    @Autowired
    private ContextMenu contextMenu;

    private Window popupWindow;

    private Button addButton;
    private Button removeButton;

    protected EntityManySelectResults() {
        super();
    }

    public abstract String getEntityCaption();

    public abstract String getPropertyId();

    public abstract EntitySingleSelect getEntitySelect();

    public void postConstruct() {
        super.postConstruct();

        addButton = new Button(uiMessageSource.getMessage("entityResults.add"), this, "add");
        addButton.addStyleName("small default");
        getResultsButtons().addComponent(addButton);

        removeButton = new Button(uiMessageSource.getMessage("entityResults.remove"), this, "remove");
        removeButton.setEnabled(false);
        removeButton.addStyleName("small default");
        getResultsButtons().addComponent(removeButton);

        getEntityTable().setMultiSelect(true);
        getEntitySelect().getEntityResults().getEntityTable().setMultiSelect(true);

        contextMenu.addAction("entityResults.remove", this, "remove");
        contextMenu.setActionEnabled("entityResults.remove", true);
        addSelectionChangedListener(this, "selectionChanged");
    }

    public void add() {
        popupWindow = new Window(entityMessageSource.getMessageWithDefault(getEntityCaption()));
        popupWindow.addStyleName("opaque");
        VerticalLayout layout = (VerticalLayout) popupWindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        popupWindow.setSizeUndefined();
        popupWindow.setModal(true);
        EntitySingleSelect entitySingleSelect = getEntitySelect();
        entitySingleSelect.getEntityResults().getEntityQuery().clear();
        entitySingleSelect.getEntityResults().search();
        popupWindow.addComponent(entitySingleSelect);
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
            value = getEntityDao().getReference(value);
            try {
                BeanUtils.setProperty(value, getPropertyId(), parent);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            getEntityDao().persist(value);
        }
        searchImpl(false);
    }

    public void valuesRemoved(Object... values) {
        for (Object value : values) {
            value = getEntityDao().getReference(value);
            try {
                BeanUtils.setProperty(value, getPropertyId(), null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            getEntityDao().persist(value);
        }
        searchImpl(false);
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

    public void selectionChanged() {
        Collection itemIds = (Collection) getEntityTable().getValue();
        if (itemIds.size() > 0) {
            getEntityTable().addActionHandler(contextMenu);
            removeButton.setEnabled(true);
        } else {
            getEntityTable().removeActionHandler(contextMenu);
            removeButton.setEnabled(false);
        }
    }
}
