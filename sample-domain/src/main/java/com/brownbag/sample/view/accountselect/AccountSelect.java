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

package com.brownbag.sample.view.accountselect;

import com.brownbag.core.view.entity.EntitySelect;
import com.brownbag.sample.dao.AccountDao;
import com.brownbag.sample.entity.Account;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("session")
public class AccountSelect extends EntitySelect<Account> {

    @Resource
    private AccountDao accountDao;

    @Resource
    private AccountQuerySelect accountQuerySelect;

    @Resource
    private AccountSearchFormSelect accountSearchFormSelect;

    @Resource
    private AccountResultsSelect accountResultsSelect;

    @Override
    public AccountDao getEntityDao() {
        return accountDao;
    }

    @Override
    public AccountQuerySelect getEntityQuery() {
        return accountQuerySelect;
    }

    @Override
    public AccountSearchFormSelect getEntitySearchForm() {
        return accountSearchFormSelect;
    }

    @Override
    public AccountResultsSelect getEntityResults() {
        return accountResultsSelect;
    }
}

