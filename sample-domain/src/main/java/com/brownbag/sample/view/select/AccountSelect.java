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

package com.brownbag.sample.view.select;

import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.core.view.entity.entityselect.EntitySelect;
import com.brownbag.core.view.entity.entityselect.EntitySelectResults;
import com.brownbag.sample.dao.AccountDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.view.account.AccountQuery;
import com.brownbag.sample.view.account.AccountSearchForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class AccountSelect extends EntitySelect<Account> {

    @Resource
    private AccountDao accountDao;

    @Resource
    private AccountQuery accountQuery;

    @Resource
    private AccountSearchForm accountSearchForm;

    @Resource
    private AccountSelectResults accountSelectResults;

    @Override
    public AccountDao getEntityDao() {
        return accountDao;
    }

    @Override
    public AccountQuery getEntityQuery() {
        return accountQuery;
    }

    @Override
    public AccountSearchForm getSearchForm() {
        return accountSearchForm;
    }

    @Override
    public AccountSelectResults getResultsComponent() {
        return accountSelectResults;
    }

    @Component
    @Scope("prototype")
    public static class AccountSelectResults extends EntitySelectResults<Account> {

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
    }
}

