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

package com.brownbag.core.view;

import com.brownbag.core.view.entity.EntityEntryPoint;
import com.vaadin.ui.TabSheet;

import java.util.List;

public abstract class MainTabSheet extends TabSheet {

    public abstract List<EntityEntryPoint> getTabEntryPoints();

    public void postConstruct() {
        List<EntityEntryPoint> entryPoints = getTabEntryPoints();
        for (EntityEntryPoint entryPoint : entryPoints) {
            addTab(entryPoint);
        }

        addListener(new TabChangeListener());
    }

    private class TabChangeListener implements SelectedTabChangeListener {

        @Override
        public void selectedTabChange(SelectedTabChangeEvent event) {
            EntityEntryPoint entityEntryPoint = (EntityEntryPoint) getSelectedTab();
            entityEntryPoint.getEntityResults().search();
        }
    }
}
