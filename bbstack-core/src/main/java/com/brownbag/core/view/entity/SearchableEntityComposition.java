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
import org.vaadin.jouni.animator.Animator;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class SearchableEntityComposition<T> extends EntityComposition<T> {
    private Button toggleSearchFormButton;
    private Animator searchFormAnimator;

    protected SearchableEntityComposition() {
        super();
    }

    public abstract EntitySearchForm getEntitySearchForm();

    public abstract EntityResultsComponent getEntityResults();

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();

        wireRelationships();
        postConstructRelatedBeans();

        toggleSearchFormButton = new Button(null, this, "toggleSearchForm");
        toggleSearchFormButton.setIcon(new ThemeResource("../customTheme/icons/collapse-icon.png"));
        toggleSearchFormButton.addStyleName("borderless");
        addComponent(toggleSearchFormButton);

        searchFormAnimator = new Animator(getEntitySearchForm());
        searchFormAnimator.setSizeUndefined();
        addComponent(searchFormAnimator);

        addComponent(getEntityResults());
    }

    private void wireRelationships() {
        getEntitySearchForm().setEntityQuery(getEntityQuery());
        getEntitySearchForm().setEntityResults(getEntityResults());
    }

    private void postConstructRelatedBeans() {
        getEntitySearchForm().postConstruct();
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
