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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.vaadin.jouni.animator.Animator;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class SearchableEntityComponent<T> extends EntityComponent {
    private Button toggleSearchFormPanelButton;
    private Animator searchFormPanelAnimator;

    protected SearchableEntityComponent() {
        super();
    }

    public abstract EntityResultsComponent getEntityResults();

    public abstract EntitySearchForm getEntitySearchForm();

    @PostConstruct
    public void postConstruct() {
        super.postConstruct();

        wireRelationships();
        postConstructRelatedBeans();

        toggleSearchFormPanelButton = new Button(getUiMessageSource().getMessage("entityTab.hide"),
                this, "toggleSearchFormPanel");
        getMainPanel().addComponent(toggleSearchFormPanelButton);

        Panel searchFormPanel = createPanel(new HorizontalLayout());
        searchFormPanelAnimator = new Animator(searchFormPanel);
        searchFormPanel.addComponent(getEntitySearchForm());
        getMainPanel().addComponent(searchFormPanelAnimator);

        Panel resultsPanel = createPanel(new VerticalLayout());
        resultsPanel.addComponent(getEntityResults());
        getMainPanel().addComponent(resultsPanel);
    }

    private void wireRelationships() {
        getEntitySearchForm().setUiMessageSource(getUiMessageSource());
        getEntitySearchForm().setEntityMessageSource(getEntityMessageSource());
        getEntitySearchForm().setEntityQuery(getEntityQuery());
        getEntitySearchForm().setEntityResults(getEntityResults());
    }

    private void postConstructRelatedBeans() {
        getEntitySearchForm().postConstruct();
    }

    public void toggleSearchFormPanel() {
        searchFormPanelAnimator.setRolledUp(!searchFormPanelAnimator.isRolledUp());
        if (searchFormPanelAnimator.isRolledUp()) {
            toggleSearchFormPanelButton.setCaption(getUiMessageSource().getMessage("entityTab.show"));
        } else {
            toggleSearchFormPanelButton.setCaption(getUiMessageSource().getMessage("entityTab.hide"));
        }
    }
}
