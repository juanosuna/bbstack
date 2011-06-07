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
import com.brownbag.core.view.MessageSource;
import com.brownbag.core.view.entity.util.ContextMenu;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResults<T> extends EntityResultsComponent<T> {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Autowired
    private ContextMenu contextMenu;

    private Button editButton;
    private Button deleteButton;

    protected EntityResults() {
        super();
    }

    public void postConstruct() {
        super.postConstruct();

        getEntityTable().setMultiSelect(true);

        Button newButton = new Button(uiMessageSource.getMessage("entityResults.new"), this, "create");
        newButton.addStyleName("small default");
        getResultsButtons().addComponent(newButton);

        editButton = new Button(uiMessageSource.getMessage("entityResults.edit"), this, "edit");
        editButton.setEnabled(false);
        editButton.addStyleName("small default");
        getResultsButtons().addComponent(editButton);

        deleteButton = new Button(uiMessageSource.getMessage("entityResults.delete"), this, "delete");
        deleteButton.setEnabled(false);
        deleteButton.addStyleName("small default");
        getResultsButtons().addComponent(deleteButton);

        addSelectionChangedListener(this, "selectionChanged");
        contextMenu.addAction("entityResults.edit", this, "edit");
        contextMenu.addAction("entityResults.delete", this, "delete");

        getEntityTable().addListener(new DoubleClickListener());
    }

    public void create() {
        getEntityForm().setEntityResults(this);
        getEntityForm().create();
    }

    public void edit() {
        Collection itemIds = (Collection) getEntityTable().getValue();
        Assert.PROGRAMMING.assertTrue(itemIds.size() == 1);
        edit(itemIds.iterator().next());
    }

    public void edit(Object itemId) {
//        getEntityTable().setValue(itemId);
        BeanItem beanItem = getEntityTable().getContainerDataSource().getItem(itemId);
        getEntityForm().setEntityResults(this);
        getEntityForm().load((WritableEntity) beanItem.getBean());
        getEntityForm().open();
    }

    public void delete() {
        Collection itemIds = (Collection) getEntityTable().getValue();
        for (Object itemId : itemIds) {
            BeanItem beanItem = getEntityTable().getContainerDataSource().getItem(itemId);
            Object entity = beanItem.getBean();
            getEntityDao().remove(entity);
        }
        // solves tricky ConcurrentModification bug where ContextMenu handler calls delete
        // but then search removes handler
        searchImpl(false);
    }

    public void selectionChanged() {
        Collection itemIds = (Collection) getEntityTable().getValue();
        if (itemIds.size() == 1) {
            contextMenu.setActionEnabled("entityResults.edit", true);
            contextMenu.setActionEnabled("entityResults.delete", true);
            getEntityTable().removeActionHandler(contextMenu);
            getEntityTable().addActionHandler(contextMenu);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else if (itemIds.size() > 1) {
            contextMenu.setActionEnabled("entityResults.edit", false);
            contextMenu.setActionEnabled("entityResults.delete", true);
            getEntityTable().removeActionHandler(contextMenu);
            getEntityTable().addActionHandler(contextMenu);
            editButton.setEnabled(false);
            deleteButton.setEnabled(true);
        } else {
            getEntityTable().removeActionHandler(contextMenu);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public class DoubleClickListener implements ItemClickEvent.ItemClickListener {
        public void itemClick(ItemClickEvent event) {
            if (event.isDoubleClick()) {
                edit(event.getItemId());
            }
        }
    }
}
