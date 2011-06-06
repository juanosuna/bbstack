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

package com.brownbag.core.view.entity.singleselect;

import com.brownbag.core.view.entity.EntityResultsComponent;
import com.brownbag.core.view.entity.util.ContextMenu;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class EntitySingleSelectResults<T> extends EntityResultsComponent<T> {

    @Autowired
    private ContextMenu contextMenu;

    private Button selectButton;

    protected EntitySingleSelectResults() {
        super();
    }

    public void postConstruct() {
        super.postConstruct();
        addSelectionChangedListener(this, "selectionChanged");

        selectButton = new Button(getUiMessageSource().getMessage("entityResults.select"));
        selectButton.setEnabled(false);
        selectButton.addStyleName("small default");
        getButtonPanel().addComponent(selectButton);
    }

    public void selectionChanged() {
        Object itemId = getEntityTable().getValue();
        if (itemId instanceof Collection) {
            if (((Collection) itemId).size() > 0) {
                selectButton.setEnabled(true);
                getEntityTable().addActionHandler(contextMenu);
            } else {
                selectButton.setEnabled(false);
                getEntityTable().removeActionHandler(contextMenu);
            }
        } else {
            if (itemId != null) {
                selectButton.setEnabled(true);
                getEntityTable().addActionHandler(contextMenu);
            } else {
                selectButton.setEnabled(false);
                getEntityTable().removeActionHandler(contextMenu);
            }
        }
    }

    public void addSelectButtonListener(Object target, String methodName) {
        selectButton.addListener(Button.ClickEvent.class, target, methodName);
        contextMenu.addAction("entityResults.select", target, methodName);
        contextMenu.setActionEnabled("entityResults.select", true);
    }
}
