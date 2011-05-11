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

import com.brownbag.core.util.ReflectionUtil;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;
import org.vaadin.jouni.animator.Animator;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityTab<T> extends Panel {
    private Button toggleSearchFormPanelButton;
    private Animator searchFormPanelAnimator;

    protected EntityTab() {
    }

    protected EntityTab(ComponentContainer content) {
        super(content);
    }

    protected EntityTab(String caption) {
        super(caption);
    }

    protected EntityTab(String caption, ComponentContainer content) {
        super(caption, content);
    }

    public Class getEntityType() {
        return ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityResults getEntityResults() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntityResults.class, getEntityType());
    }

    public EntitySearchForm getEntitySearchForm() {
        return SpringApplicationContext.getBeanByTypeAndGenericArgumentType(EntitySearchForm.class, getEntityType());
    }

    @PostConstruct
    public void init() {
        EntityResults results = getEntityResults();
        EntitySearchForm searchForm = getEntitySearchForm();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);
        setContent(layout);
        addStyleName(Runo.PANEL_LIGHT);

        toggleSearchFormPanelButton = new Button("Hide", this, "toggleSearchFormPanel");
        addComponent(toggleSearchFormPanelButton);

        Panel searchFormPanel = createPanel(new HorizontalLayout());
        searchFormPanelAnimator = new Animator(searchFormPanel);
        searchFormPanel.addComponent(searchForm);
        addComponent(searchFormPanelAnimator);

        Panel resultsPanel = createPanel(new VerticalLayout());
        resultsPanel.addComponent(results);
        addComponent(resultsPanel);
    }

    public void toggleSearchFormPanel() {
        searchFormPanelAnimator.setRolledUp(!searchFormPanelAnimator.isRolledUp());
        if (searchFormPanelAnimator.isRolledUp()) {
            toggleSearchFormPanelButton.setCaption("Show");
        } else {
            toggleSearchFormPanelButton.setCaption("Hide");
        }
    }

    private static Panel createPanel(AbstractOrderedLayout layout) {
        Panel panel = new Panel();
        panel.addStyleName(Runo.PANEL_LIGHT);
        layout.setMargin(false);
        layout.setSpacing(true);
        panel.setContent(layout);

        return panel;
    }

    public abstract String displayName();
}
