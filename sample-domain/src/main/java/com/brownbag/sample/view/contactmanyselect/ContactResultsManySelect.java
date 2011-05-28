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

package com.brownbag.sample.view.contactmanyselect;

import com.brownbag.core.view.entity.EntityResultsManySelect;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.view.contactselect.ContactSelect;
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
public class ContactResultsManySelect extends EntityResultsManySelect<Contact> {

    @Resource
    private ContactQueryManySelect contactQueryManySelect;

    @Resource
    private ContactDao contactDao;

    @Resource
    private ContactSelect contactSelect;

    @Override
    public void configureEntityFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "address.state",
                "address.country"
        });
    }

    @Override
    public ContactSelect getEntitySelect() {
        return contactSelect;
    }

    @Override
    public void valueSelected(Object value) {
        Contact contactSelected = (Contact) value;
        Account account = contactQueryManySelect.getAccount();
        contactSelected = contactDao.find(contactSelected.getId());
        contactSelected.setAccount(account);
        contactDao.persist(contactSelected);
        search();
    }

    @Override
    public void valueRemoved(Object value) {
        Contact contactSelected = (Contact) value;
        contactSelected = contactDao.find(contactSelected.getId());
        contactSelected.setAccount(null);
        contactDao.persist(contactSelected);
        search();
    }
}
