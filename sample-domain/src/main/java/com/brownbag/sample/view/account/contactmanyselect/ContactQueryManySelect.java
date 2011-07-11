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

import com.brownbag.core.view.entity.manyselect.ManySelectQuery;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Contact;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
@Component
@Scope("session")
public class ContactQueryManySelect extends ManySelectQuery<Contact, Account> {

    @Resource
    private ContactDao contactDao;

    @Override
    public List<Contact> execute() {
        return contactDao.find(this);
    }
}
