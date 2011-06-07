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

import com.brownbag.core.view.entity.manyselect.ManySelect;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Contact;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("session")
public class ContactManySelect extends ManySelect<Contact> {

    @Resource
    private ContactDao contactDao;

    @Resource
    private ContactManySelectResults contactResultsManySelect;

    @Resource
    private ContactQueryManySelect contactQueryManySelect;

    @Override
    public String getEntityCaption() {
        return "Contacts";
    }

    @Override
    public ContactDao getEntityDao() {
        return contactDao;
    }

    @Override
    public ContactQueryManySelect getEntityQuery() {
        return contactQueryManySelect;
    }

    @Override
    public ContactManySelectResults getResultsComponent() {
        return contactResultsManySelect;
    }
}

