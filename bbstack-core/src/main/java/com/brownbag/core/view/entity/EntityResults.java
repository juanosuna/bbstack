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
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResults<T> extends EntityResultsComponent {

    private EntityForm entityForm;
    private EntityQuery entityQuery;
    private ContextMenu contextMenu;

    protected EntityResults() {
        super();
    }

    EntityForm getEntityForm() {
        return entityForm;
    }

    void setEntityForm(EntityForm entityForm) {
        this.entityForm = entityForm;
    }

    public EntityQuery getEntityQuery() {
        return entityQuery;
    }

    void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    public void postConstruct() {
        super.postConstruct();

        Button newButton = new Button(getUiMessageSource().getMessage("entityResults.new"), this, "create");
        newButton.addStyleName("small default");
        getButtonPanel().addComponent(newButton);

        addSelectionChangedListener(this, "selectionChanged");
        contextMenu = new ContextMenu();

        getEntityTable().addListener(new EntityResults.DoubleClickListener());
    }

    public void create() {
        getEntityForm().setEntityResults(this);
        getEntityForm().create();
    }

    public void edit() {
        Object itemId = getEntityTable().getValue();
        edit(itemId);
    }

    public void edit(Object itemId) {
        getEntityTable().setValue(itemId);
        BeanItem beanItem = getEntityTable().getContainerDataSource().getItem(itemId);
        getEntityForm().setEntityResults(this);
        getEntityForm().load((WritableEntity) beanItem.getBean());
        getEntityForm().open();
    }

    public void delete() {
        Object itemId = getEntityTable().getValue();
        if (itemId != null) {
            BeanItem beanItem = getEntityTable().getContainerDataSource().getItem(itemId);
            Object entity = beanItem.getBean();
            getEntityDao().remove(entity);
            search();
        }
    }

    public void selectionChanged(Property.ValueChangeEvent event) {
        Object itemId = getEntityTable().getValue();
        if (itemId != null) {
            getEntityTable().addActionHandler(contextMenu);
        } else {
            getEntityTable().removeActionHandler(contextMenu);
        }
    }

    public class ContextMenu implements Action.Handler {
        private Action editAction = new Action(getUiMessageSource().getMessage("entityResults.edit"));
        private Action deleteAction = new Action(getUiMessageSource().getMessage("entityResults.delete"));
        private Action[] allActions = new Action[]{editAction, deleteAction};

        @Override
        public Action[] getActions(Object target, Object sender) {
            return allActions;
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            if (editAction == action) {
                edit();
            } else if (deleteAction == action) {
                delete();
            }
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
