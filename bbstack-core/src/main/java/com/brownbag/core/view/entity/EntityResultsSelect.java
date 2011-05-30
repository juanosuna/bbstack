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

import com.vaadin.data.Property;
import com.vaadin.ui.Button;

import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntityResultsSelect<T> extends EntityResultsComponent {

    private Button selectButton;

    protected EntityResultsSelect() {
        super();
    }

    public void postConstruct() {
        super.postConstruct();
        addSelectionChangedListener(this, "selectionChanged");

        selectButton = new Button(getUiMessageSource().getMessage("entityResults.select"));
        selectButton.addStyleName("small default");
        selectButton.setEnabled(false);
        getButtonPanel().addComponent(selectButton);
    }

    public void selectionChanged(Property.ValueChangeEvent event) {
        Object itemId = getEntityTable().getValue();
        if (itemId instanceof Collection) {
            selectButton.setEnabled(((Collection) itemId).size() > 0);
        } else {
            selectButton.setEnabled(itemId != null);
        }
    }

    public void addSelectButtonListener(Object target, String methodName) {
        selectButton.addListener(Button.ClickEvent.class, target, methodName);
    }
}
