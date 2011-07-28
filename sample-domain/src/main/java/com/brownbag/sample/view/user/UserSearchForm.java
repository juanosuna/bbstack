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

import com.brownbag.core.view.entity.SearchForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.view.contact.ContactQuery;
import com.vaadin.data.Property;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class UserSearchForm extends SearchForm<UserQuery> {

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("loginName", 1, 1);
    }

    @Override
    public String getEntityCaption() {
        return "User Search Form";
    }
}
