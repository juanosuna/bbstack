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

import com.brownbag.core.view.entity.manyselect.EntityManySelectResults;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.view.account.contactmanyselect.contactselect.ContactSingleSelect;
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
public class ContactManySelectResults extends EntityManySelectResults<Contact> {

    @Resource
    private ContactSingleSelect contactSelect;

    @Override
    public void configureEntityFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "address.state",
                "address.country",
                "lastModified",
                "lastModifiedBy"
        });
    }

    @Override
    public ContactSingleSelect getEntitySelect() {
        return contactSelect;
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
