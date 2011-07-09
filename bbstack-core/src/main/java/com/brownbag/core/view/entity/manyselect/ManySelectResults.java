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
import com.brownbag.core.view.entity.ResultsComponent;
import com.brownbag.core.view.entity.singleselect.SingleSelect;
import com.brownbag.core.view.entity.util.ActionContextMenu;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class ManySelectResults<T> extends ResultsComponent<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource(name = "entityMessageSource")
    private MessageSource entityMessageSource;

    @Autowired
    private ActionContextMenu contextMenu;

    private Window popupWindow;

    private Button addButton;
    private Button removeButton;

    protected ManySelectResults() {
        super();
    }

    public abstract String getEntityCaption();

    public abstract String getPropertyId();

    public abstract SingleSelect getSingleSelect();

    public void postConstruct() {
        super.postConstruct();

        HorizontalLayout crudButtons = new HorizontalLayout();
        crudButtons.setMargin(false);
        crudButtons.setSpacing(true);

        addButton = new Button(uiMessageSource.getMessage("entityResults.add"), this, "add");
        addButton.setIcon(new ThemeResource("icons/16/add.png"));
        addButton.addStyleName("small default");
        crudButtons.addComponent(addButton);

        removeButton = new Button(uiMessageSource.getMessage("entityResults.remove"), this, "remove");
        removeButton.setIcon(new ThemeResource("icons/16/delete.png"));
        removeButton.setEnabled(false);
        removeButton.addStyleName("small default");
        crudButtons.addComponent(removeButton);

        getCrudButtons().addComponent(crudButtons, 0);
        getCrudButtons().setComponentAlignment(crudButtons, Alignment.MIDDLE_LEFT);

        getResultsTable().setMultiSelect(true);
        getSingleSelect().getResultsComponent().getResultsTable().setMultiSelect(true);

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
        SingleSelect singleSelect = getSingleSelect();
        singleSelect.getResultsComponent().getEntityQuery().clear();
        singleSelect.getResultsComponent().search();
        popupWindow.addComponent(singleSelect);
        popupWindow.setClosable(true);
        getSingleSelect().getResultsComponent().addSelectButtonListener(this, "itemsSelected");
        MainApplication.getInstance().getMainWindow().addWindow(popupWindow);
    }

    public void itemsSelected() {
        close();
        Collection selectedValues = getSingleSelect().getResultsComponent().getSelectedValues();
        valuesSelected(selectedValues.toArray());
    }

    @Override
    public ManySelectQuery getEntityQuery() {
        return (ManySelectQuery) super.getEntityQuery();
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
        removeButton.setEnabled(false);
    }

    public void setAddButtonEnabled(boolean isEnabled) {
        addButton.setEnabled(isEnabled);
    }

    public void close() {
        MainApplication.getInstance().getMainWindow().removeWindow(popupWindow);
    }

    public void removeImpl() {
        Collection selectedValues = getSelectedValues();
        valuesRemoved(selectedValues.toArray());
    }

    public void remove() {
        ConfirmDialog dialog = ConfirmDialog.show(MainApplication.getInstance().getMainWindow(),
                uiMessageSource.getMessage("entityResults.confirmationCaption"),
                uiMessageSource.getMessage("entityResults.confirmationPrompt"),
                uiMessageSource.getMessage("entityResults.confirmationYes"),
                uiMessageSource.getMessage("entityResults.confirmationNo"),
                new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            removeImpl();
                        }
                    }
                });
    }

    public void selectionChanged() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        if (itemIds.size() > 0) {
            getResultsTable().addActionHandler(contextMenu);
            removeButton.setEnabled(true);
        } else {
            getResultsTable().removeActionHandler(contextMenu);
            removeButton.setEnabled(false);
        }
    }
}
