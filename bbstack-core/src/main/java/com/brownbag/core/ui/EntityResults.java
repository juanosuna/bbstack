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
import com.brownbag.core.query.EntityQuery;
import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.POJOItem;
import com.vaadin.event.Action;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResults<T> extends Panel {

    private EntityDao entityDao;

    private EntityTable entityTable;

    private EntityForm entityForm;

    private EntityQuery entityQuery;

    protected EntityResults() {
    }

    protected EntityResults(ComponentContainer content) {
        super(content);
    }

    protected EntityResults(String caption) {
        super(caption);
    }

    protected EntityResults(String caption, ComponentContainer content) {
        super(caption, content);
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityDao getEntityDao() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityDao.class, getEntityType());
    }

    public EntityTable getEntityTable() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityTable.class, getEntityType());
    }

    public EntityForm getEntityForm() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityForm.class, getEntityType());
    }

    public EntityQuery getEntityQuery() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityQuery.class, getEntityType());
    }

    @PostConstruct
    public void init() {
        entityDao = getEntityDao();
        entityTable = getEntityTable();
        entityForm = getEntityForm();
        entityQuery = getEntityQuery();

        Panel buttonPanel = createButtonPanel();
        addComponent(buttonPanel);
        addComponent(entityTable);

        entityTable.addActionHandler(new ContextMenu());
        search();
    }

    private Panel createButtonPanel() {
        Panel buttonPanel = new Panel();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
        buttonPanel.setSizeUndefined();
        buttonPanel.addStyleName(Runo.PANEL_LIGHT);
        buttonPanel.setContent(layout);

        Button firstButton = new Button("First", entityTable, "firstPage");
        buttonPanel.addComponent(firstButton);

        Button previousButton = new Button("Previous", entityTable, "previousPage");
        buttonPanel.addComponent(previousButton);

        Button nextButton = new Button("Next", entityTable, "nextPage");
        buttonPanel.addComponent(nextButton);

        Button lastButton = new Button("Last", entityTable, "lastPage");
        buttonPanel.addComponent(lastButton);

        Label pageSizeLabel = new Label("Page Size: ");
        buttonPanel.addComponent(pageSizeLabel);
        ComboBox pageSizeMenu = new ComboBox();
        pageSizeMenu.addItem(10);
        pageSizeMenu.addItem(25);
        pageSizeMenu.addItem(50);
        pageSizeMenu.addItem(100);
        MethodProperty pageProperty = new MethodProperty(entityQuery, "pageSize");
        pageSizeMenu.setPropertyDataSource(pageProperty);
        pageSizeMenu.setNullSelectionAllowed(false);
        pageSizeMenu.setImmediate(true);
        pageSizeMenu.setWidth(5, UNITS_EM);
        pageSizeMenu.addListener(Property.ValueChangeEvent.class, this, "search");
        buttonPanel.addComponent(pageSizeMenu);

        Button newButton = new Button("New", this, "create");
        buttonPanel.addComponent(newButton);

        return buttonPanel;
    }

    public void create() {
        entityForm.create();
    }

    public void edit() {
        Object itemId = entityTable.getValue();
        POJOItem personItem = (POJOItem) entityTable.getContainerDataSource().getItem(itemId);
        entityForm.load((WritableEntity) personItem.getBean());
        entityForm.open();
    }

    public void delete() {
        Object itemId = entityTable.getValue();
        if (itemId != null) {
            POJOItem personItem = (POJOItem) entityTable.getContainerDataSource().getItem(itemId);
            Object entity = personItem.getBean();
            entityDao.delete(entity);
            search();
        }
    }

    public void search() {
        entityTable.search();
        String caption = "Found " + entityQuery.getResultCount()
                + " results. Right-mouse click row to edit or delete";
        entityTable.setCaption(caption);
    }

    public class ContextMenu implements Action.Handler {
        private Action editAction = new Action("Edit");
        private Action deleteAction = new Action("Delete");
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
}
