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

import com.brownbag.core.entity.WritableEntity;
import com.brownbag.core.util.assertion.Assert;
import com.brownbag.core.view.MainApplication;
import com.brownbag.core.view.MessageSource;
import com.brownbag.core.view.entity.util.ActionContextMenu;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class Results<T> extends ResultsComponent<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Autowired
    private ActionContextMenu contextMenu;

    private Button editButton;
    private Button deleteButton;

    protected Results() {
        super();
    }

    public void postConstruct() {
        super.postConstruct();

        getResultsTable().setMultiSelect(true);

        HorizontalLayout crudButtons = new HorizontalLayout();
        crudButtons.setMargin(false);
        crudButtons.setSpacing(true);

        Button newButton = new Button(uiMessageSource.getMessage("entityResults.new"), this, "create");
        newButton.setDescription(uiMessageSource.getMessage("entityResults.new.description"));
        newButton.setIcon(new ThemeResource("icons/16/add.png"));
        newButton.addStyleName("small default");
        crudButtons.addComponent(newButton);

        editButton = new Button(uiMessageSource.getMessage("entityResults.edit"), this, "edit");
        editButton.setDescription(uiMessageSource.getMessage("entityResults.edit.description"));
        editButton.setIcon(new ThemeResource("icons/16/edit.png"));
        editButton.setEnabled(false);
        editButton.addStyleName("small default");
        crudButtons.addComponent(editButton);

        deleteButton = new Button(uiMessageSource.getMessage("entityResults.delete"), this, "delete");
        deleteButton.setDescription(uiMessageSource.getMessage("entityResults.delete.description"));
        deleteButton.setIcon(new ThemeResource("icons/16/delete.png"));
        deleteButton.setEnabled(false);
        deleteButton.addStyleName("small default");
        crudButtons.addComponent(deleteButton);

        addSelectionChangedListener(this, "selectionChanged");
        contextMenu.addAction("entityResults.edit", this, "edit");
        contextMenu.addAction("entityResults.delete", this, "delete");

        getCrudButtons().addComponent(crudButtons, 0);
        getCrudButtons().setComponentAlignment(crudButtons, Alignment.MIDDLE_LEFT);

        getResultsTable().addListener(new DoubleClickListener());
    }

    public void create() {
        getEntityForm().setResults(this);
        getEntityForm().create();
    }

    public void edit() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        Assert.PROGRAMMING.assertTrue(itemIds.size() == 1);
        editImpl(itemIds.iterator().next());
    }

    public void editImpl(Object itemId) {
        loadItem(itemId);
        getEntityForm().open(true);
    }

    private Object currentItemId;

    public void loadItem(Object itemId) {
        currentItemId = itemId;
        BeanItem beanItem = getResultsTable().getContainerDataSource().getItem(itemId);
        getEntityForm().setResults(this);
        getEntityForm().load((WritableEntity) beanItem.getBean());
    }

    void editPreviousItem() {
        Object previousItemId = getResultsTable().getContainerDataSource().prevItemId(currentItemId);
        if (previousItemId == null && getEntityQuery().hasPreviousPage()) {
            getResultsTable().previousPage();
            previousItemId = getResultsTable().getContainerDataSource().lastItemId();
        }
        if (previousItemId != null) {
            loadItem(previousItemId);
        }
    }

    boolean hasPreviousItem() {
        Object previousItemId = getResultsTable().getContainerDataSource().prevItemId(currentItemId);
        return previousItemId != null || getEntityQuery().hasPreviousPage();
    }

    void editNextItem() {
        Object nextItemId = getResultsTable().getContainerDataSource().nextItemId(currentItemId);
        if (nextItemId == null && getEntityQuery().hasNextPage()) {
            getResultsTable().nextPage();
            nextItemId = getResultsTable().getContainerDataSource().firstItemId();
        }

        if (nextItemId != null) {
            loadItem(nextItemId);
        }
    }

    boolean hasNextItem() {
        Object nextItemId = getResultsTable().getContainerDataSource().nextItemId(currentItemId);
        return nextItemId != null || getEntityQuery().hasNextPage();
    }

    public void deleteImpl() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        for (Object itemId : itemIds) {
            BeanItem beanItem = getResultsTable().getContainerDataSource().getItem(itemId);
            Object entity = beanItem.getBean();
            getEntityDao().remove(entity);
        }

        // solves tricky ConcurrentModification bug where ContextMenu handler calls delete
        // but then search removes handler
        searchImpl(false);
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
    }

    public void delete() {
        ConfirmDialog dialog = ConfirmDialog.show(MainApplication.getInstance().getMainWindow(),
                uiMessageSource.getMessage("entityResults.confirmationCaption"),
                uiMessageSource.getMessage("entityResults.confirmationPrompt"),
                uiMessageSource.getMessage("entityResults.confirmationYes"),
                uiMessageSource.getMessage("entityResults.confirmationNo"),
                new ConfirmDialog.Listener() {
                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            deleteImpl();
                        }
                    }
                });
    }

    public void selectionChanged() {
        Collection itemIds = (Collection) getResultsTable().getValue();
        if (itemIds.size() == 1) {
            contextMenu.setActionEnabled("entityResults.edit", true);
            contextMenu.setActionEnabled("entityResults.delete", true);
            getResultsTable().removeActionHandler(contextMenu);
            getResultsTable().addActionHandler(contextMenu);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else if (itemIds.size() > 1) {
            contextMenu.setActionEnabled("entityResults.edit", false);
            contextMenu.setActionEnabled("entityResults.delete", true);
            getResultsTable().removeActionHandler(contextMenu);
            getResultsTable().addActionHandler(contextMenu);
            editButton.setEnabled(false);
            deleteButton.setEnabled(true);
        } else {
            getResultsTable().removeActionHandler(contextMenu);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public class DoubleClickListener implements ItemClickEvent.ItemClickListener {
        public void itemClick(ItemClickEvent event) {
            if (event.isDoubleClick()) {
                editImpl(event.getItemId());
            }
        }
    }
}
