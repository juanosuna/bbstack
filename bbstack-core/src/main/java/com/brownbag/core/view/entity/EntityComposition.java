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
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityComposition<T> extends CustomComponent {

    @Resource(name = "uiMessageSource")
    private MessageSource uiMessageSource;

    @Resource(name = "entityMessageSource")
    private MessageSource entityMessageSource;

    private Panel mainPanel;

    protected EntityComposition() {
    }

    public abstract EntityDao getEntityDao();
    public abstract EntityQuery getEntityQuery();
    public abstract EntityResultsComponent getEntityResults();
    public abstract String getEntityCaption();

    public String getCaption() {
        return getEntityMessageSource().getMessageWithDefault(getEntityCaption());
    }

    public Panel getMainPanel() {
        return mainPanel;
    }

    public MessageSource getUiMessageSource() {
        return uiMessageSource;
    }

    public MessageSource getEntityMessageSource() {
        return entityMessageSource;
    }

    @PostConstruct
    public void postConstruct() {
        wireRelationships();
        postConstructRelatedBeans();

        VerticalLayout layout = new VerticalLayout();
        mainPanel = createPanel(layout);

        setCompositionRoot(mainPanel);
    }

    private void wireRelationships() {
        getEntityResults().setUiMessageSource(uiMessageSource);
        getEntityResults().setEntityMessageSource(entityMessageSource);
        getEntityResults().setEntityDao(getEntityDao());
        getEntityResults().setEntityQuery(getEntityQuery());
    }

    private void postConstructRelatedBeans() {
        getEntityResults().postConstruct();
    }

    protected static Panel createPanel(AbstractOrderedLayout layout) {
        Panel panel = new Panel();
        panel.addStyleName("borderless");
        layout.setMargin(false);
        layout.setSpacing(true);
        panel.setContent(layout);

        return panel;
    }
}
