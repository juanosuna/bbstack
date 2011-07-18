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

package com.brownbag.sample.view.contact;

import com.brownbag.core.view.entity.Results;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.view.account.AccountForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: Juan
 * Date: 5/6/11
 * Time: 4:04 PM
 */
@Component
@Scope("prototype")
public class ContactResults extends Results<Contact> {

    @Resource
    private ContactDao contactDao;

    @Resource
    private ContactQuery contactQuery;

    @Resource
    private ContactForm contactForm;

    @Resource
    private AccountForm accountForm;

    @Override
    public ContactDao getEntityDao() {
        return contactDao;
    }

    @Override
    public ContactQuery getEntityQuery() {
        return contactQuery;
    }

    @Override
    public ContactForm getEntityForm() {
        return contactForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "firstName",
                "lastName",
                "account.name",
                "address.state.code",
                "address.country",
                "lastModified",
                "modifiedBy"
        });

        displayFields.getField("address.state.code").setLabel("State");
        displayFields.getField("account.name").setLabel("Account");
        displayFields.getField("account.name").setFormLink("account", accountForm);
    }
}
