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

package com.brownbag.sample.view.account.contactmanyselect.contactselect;

import com.brownbag.core.view.entity.singleselect.SingleSelect;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Contact;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("session")
public class ContactSingleSelect extends SingleSelect<Contact> {

    @Resource
    private ContactDao contactDao;

    @Resource
    private ContactQuerySingleSelect contactQuerySelect;

    @Resource
    private ContactSearchFormSelect contactSearchFormSelect;

    @Resource
    private ContactSingleSelectResults contactResultsSelect;

    @Override
    public ContactDao getEntityDao() {
        return contactDao;
    }

    @Override
    public ContactQuerySingleSelect getEntityQuery() {
        return contactQuerySelect;
    }

    @Override
    public ContactSearchFormSelect getSearchForm() {
        return contactSearchFormSelect;
    }

    @Override
    public ContactSingleSelectResults getResultsComponent() {
        return contactResultsSelect;
    }
}

