/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.core.view.entity;

import com.brownbag.core.dao.EntityQuery;
import com.brownbag.core.entity.WritableEntity;
import com.brownbag.core.view.entity.field.DisplayField;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.NullCapableBeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ResultsTable extends Table {


    private ResultsComponent results;

    protected ResultsTable(ResultsComponent results) {
        this.results = results;
        addStyleName("strong striped");
        initialize();
    }

    public DisplayFields getEntityFields() {
        return results.getDisplayFields();
    }

    public Class getEntityType() {
        return results.getEntityType();
    }

    public void initialize() {
        setSizeUndefined();
        setEditable(true);
        setTableFieldFactory(new TableButtonLinkFactory());

        NullCapableBeanItemContainer dataSource = new NullCapableBeanItemContainer(getEntityType(),
                results.getDisplayFields());
        dataSource.setNonSortablePropertyIds(results.getDisplayFields().getNonSortablePropertyIds());
        String[] propertyIds = getEntityFields().getPropertyIdsAsArray();
        for (String propertyId : propertyIds) {
            dataSource.addNestedContainerProperty(propertyId);
        }
        setContainerDataSource(dataSource);

        setSelectable(true);
        setImmediate(true);
        setColumnReorderingAllowed(true);
        setColumnCollapsingAllowed(true);
        setCacheRate(1);

        setVisibleColumns(getEntityFields().getPropertyIdsAsArray());
        setColumnHeaders(getEntityFields().getLabelsAsArray());
    }

    @Override
    public BeanItemContainer getContainerDataSource() {
        return (BeanItemContainer) super.getContainerDataSource();
    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
        if (propertyId.length > 1) {
            throw new RuntimeException("Cannot sort on more than one column");
        } else if (propertyId.length == 1) {
            if (results.getDisplayFields().getField(propertyId[0].toString()).isSortable()) {
                results.getEntityQuery().setOrderByPropertyId(propertyId[0].toString());
                if (ascending[0]) {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.ASC);
                } else {
                    results.getEntityQuery().setOrderDirection(EntityQuery.OrderDirection.DESC);
                }
                firstPage();
            } else {
                throw new UnsupportedOperationException("No sorting on this column");
            }
        }
    }

    public int getFirstResult() {
        EntityQuery query = results.getEntityQuery();
        return query.getResultCount() == 0 ? 0 : query.getFirstResult() + 1;
    }

    public void setFirstResult(int firstResult) {
        clearSelection();
        results.getEntityQuery().setFirstResult(firstResult - 1);
        executeCurrentQuery();
    }

    public void refresh() {
        clearSelection();
        executeCurrentQuery();
    }

    public void firstPage() {
        clearSelection();
        results.getEntityQuery().firstPage();
        executeCurrentQuery();
    }

    public void previousPage() {
        clearSelection();
        results.getEntityQuery().previousPage();
        executeCurrentQuery();
    }

    public void nextPage() {
        clearSelection();
        results.getEntityQuery().nextPage();
        executeCurrentQuery();
    }

    public void lastPage() {
        clearSelection();
        results.getEntityQuery().lastPage();
        executeCurrentQuery();
    }

    public void executeCurrentQuery() {
        List entities = results.getEntityQuery().execute();
        getContainerDataSource().removeAllItems();
        for (Object entity : entities) {
            getContainerDataSource().addBean(entity);
        }

        results.refreshResultCountLabel();
        results.refreshNavigationButtonStates();
//        setPageLength(0);
        setPageLength(Math.min(entities.size(), results.getPageSize()));
    }

    public void clearSelection() {
        if (isMultiSelect()) {
            setValue(new HashSet());
        } else {
            setValue(null);
        }
    }

    @Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {

        Format format = null;

        if (property.getValue() != null) {
            DisplayField displayField = results.getDisplayFields().getField(colId.toString());
            format = displayField.getFormat();
        }

        if (format == null) {
            return super.formatPropertyValue(rowId, colId, property);
        } else {
            return format.format(property.getValue());
        }
    }

    public class TableButtonLinkFactory implements TableFieldFactory {
        public Field createField(Container container, Object itemId,
                                 Object propertyId, Component uiContext) {

            DisplayField.FormLink formLink = results.getDisplayFields().getField(propertyId.toString()).getFormLink();

            if (formLink != null) {
                BeanItem item = getContainerDataSource().getItem(itemId);
                Button button = new ButtonLink(item.getItemProperty(propertyId));
                button.addListener(new ButtonLinkClickListener(formLink, item));
                return button;
            }

            return null;
        }
    }

    public class ButtonLinkClickListener implements Button.ClickListener {
        private DisplayField.FormLink formLink;
        private BeanItem item;

        public ButtonLinkClickListener(DisplayField.FormLink formLink, BeanItem item) {
            this.formLink = formLink;
            this.item = item;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            Object parentBean = item.getBean();
            try {
                WritableEntity propertyBean = (WritableEntity) PropertyUtils.getProperty(parentBean,
                        formLink.getPropertyId());
                EntityForm entityForm = formLink.getEntityForm();
                entityForm.setCloseListener(results, "search");
                entityForm.load(propertyBean);
                entityForm.open(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ButtonLink extends Button {
        private Property itemProperty;

        public ButtonLink(Property itemProperty) {
            this.itemProperty = itemProperty;
            setStyleName(BaseTheme.BUTTON_LINK);
        }

        @Override
        public String getCaption() {
            if (itemProperty.getValue() == null) {
                return null;
            } else {
                return itemProperty.getValue().toString();
            }
        }

        @Override
        protected void setInternalValue(Object newValue) {
            super.setInternalValue(false);
        }

        @Override
        protected void setValue(Object newValue, boolean repaintIsNotNeeded) throws ReadOnlyException, ConversionException {
            super.setValue(false, repaintIsNotNeeded);
        }

        @Override
        public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
            super.setValue(false);
        }

        @Override
        public void setPropertyDataSource(Property newDataSource) {
            itemProperty = newDataSource;
        }
    }
}
