/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.view.user;

import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.field.SelectField;
import com.brownbag.sample.dao.UserDao;
import com.brownbag.sample.entity.*;
import com.brownbag.sample.view.select.AccountSelect;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class UserForm extends EntityForm<User> {

    @Resource
    private UserDao userDao;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("loginName", 1, 1);
        formFields.setPosition("loginPassword", 2, 1);
    }


    @Override
    public String getEntityCaption() {
        return "User Form";
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(48, Sizeable.UNITS_EM);
        popupWindow.setHeight(30, Sizeable.UNITS_EM);
    }
}
