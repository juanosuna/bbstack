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

import com.brownbag.core.ui.EntityTab;
import com.brownbag.core.util.SpringApplicationContext;
import com.vaadin.ui.TabSheet;
import org.springframework.context.annotation.Scope;

import java.util.Set;

@org.springframework.stereotype.Component
@Scope("session")
public class MainTabSheet extends TabSheet {
    private static final long serialVersionUID = 1L;

    public void init() {
        Set<EntityTab> tabs = SpringApplicationContext.getBeansByType(EntityTab.class);
        for (EntityTab tab : tabs) {
            addTab(tab, tab.displayName(), null);
        }
    }
}
