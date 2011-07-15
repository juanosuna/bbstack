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

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.jouni.animator.Animator;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntryPoint<T> extends EntityComponent<T> {
    private Button toggleSearchFormButton;
    private Animator searchFormAnimator;

    protected EntryPoint() {
        super();
    }

    public abstract SearchForm getSearchForm();

    public abstract ResultsComponent<T> getResultsComponent();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        wireRelationships();

        HorizontalLayout searchFormLayout = new HorizontalLayout();
        searchFormLayout.setMargin(false, false, true, false);
        toggleSearchFormButton = new Button(null, this, "toggleSearchForm");
        toggleSearchFormButton.setDescription(uiMessageSource.getMessage("entryPoint.toggleSearchForm.description"));
        toggleSearchFormButton.setIcon(new ThemeResource("../customTheme/icons/collapse-icon.png"));
        toggleSearchFormButton.addStyleName("borderless");
        searchFormLayout.addComponent(toggleSearchFormButton);

        searchFormAnimator = new Animator(getSearchForm());
        searchFormAnimator.setSizeUndefined();
        searchFormLayout.addComponent(searchFormAnimator);

        addComponent(searchFormLayout);
        addComponent(getResultsComponent());
    }

    private void wireRelationships() {
        getSearchForm().setResults(getResultsComponent());
        getSearchForm().postWire();
    }

    public void toggleSearchForm() {
        searchFormAnimator.setRolledUp(!searchFormAnimator.isRolledUp());
        // todo beautify icons
        if (searchFormAnimator.isRolledUp()) {
            toggleSearchFormButton.setIcon(new ThemeResource("../customTheme/icons/expand-icon.png"));
        } else {
            toggleSearchFormButton.setIcon(new ThemeResource("../customTheme/icons/collapse-icon.png"));
        }
    }
}
