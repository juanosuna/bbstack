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
import com.brownbag.core.view.MessageSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityComponent<T> extends CustomComponent {

    @Resource
    protected MessageSource entityMessageSource;

    @Resource
    protected MessageSource uiMessageSource;

    protected EntityComponent() {
    }

    public abstract EntityDao getEntityDao();

    public abstract EntityQuery getEntityQuery();

    public abstract ResultsComponent getResultsComponent();

    public abstract String getEntityCaption();

    public String getCaption() {
        return entityMessageSource.getMessageWithDefault(getEntityCaption());
    }

    @PostConstruct
    public void postConstruct() {
        wireRelationships();
        postConstructRelatedBeans();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setCompositionRoot(layout);

        setCustomSizeUndefined();
    }

    private void wireRelationships() {
        getResultsComponent().setEntityDao(getEntityDao());
        getResultsComponent().setEntityQuery(getEntityQuery());
    }

    private void postConstructRelatedBeans() {
        getResultsComponent().postConstruct();
    }

    public void setCustomSizeUndefined() {
        setSizeUndefined();
        getCompositionRoot().setSizeUndefined();
    }

    @Override
    public void addComponent(Component c) {
        ((ComponentContainer) getCompositionRoot()).addComponent(c);
    }
}
