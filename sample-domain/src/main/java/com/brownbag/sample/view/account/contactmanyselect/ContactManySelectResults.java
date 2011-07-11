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

package com.brownbag.sample.view.account.contactmanyselect;

import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.core.view.entity.manyselect.ManySelectResults;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.view.account.contactmanyselect.contactsingleselect.ContactSingleSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: Juan
 * Date: 5/6/11
 * Time: 4:04 PM
 */
@Component
@Scope("session")
public class ContactManySelectResults extends ManySelectResults<Contact> {

    @Resource
    private ContactSingleSelect contactSingleSelect;

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "address.state",
                "address.country",
                "lastModified",
                "modifiedBy"
        });

        displayFields.getField("name").setSortable(false);
    }

    @Override
    public ContactSingleSelect getSingleSelect() {
        return contactSingleSelect;
    }

    @Override
    public String getPropertyId() {
        return "account";
    }

    @Override
    public String getEntityCaption() {
        return "Contacts";
    }
}
