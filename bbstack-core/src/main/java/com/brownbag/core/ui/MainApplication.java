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

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainApplication extends Application implements HttpServletRequestListener {
    private static final long serialVersionUID = 1L;

    private static ThreadLocal<MainApplication> threadLocal = new ThreadLocal<MainApplication>();

    @Autowired
    private MainTabSheet tabSheet;

    private Window mainWindow;

    public static MainApplication getInstance() {
        return threadLocal.get();
    }

    public static void setInstance(MainApplication application) {
        if (getInstance() == null) {
            threadLocal.set(application);
        }
    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        MainApplication.setInstance(this);
    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        threadLocal.remove();
    }

    @Override
    public void init() {
        setInstance(this);

        setTheme("mytheme");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainWindow = new Window("Sample Application");
        mainWindow.setContent(mainLayout);
        setMainWindow(mainWindow);

        tabSheet.init();
        mainWindow.addComponent(tabSheet);
    }

    @Override
    public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
        if (event.getThrowable().getCause() instanceof AccessDeniedException) {
            getMainWindow().showNotification("Access Denied", Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }
}
