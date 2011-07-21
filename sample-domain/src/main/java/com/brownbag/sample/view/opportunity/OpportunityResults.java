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

package com.brownbag.sample.view.opportunity;

import com.brownbag.core.view.entity.Results;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.OpportunityDao;
import com.brownbag.sample.entity.Opportunity;
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
public class OpportunityResults extends Results<Opportunity> {

    @Resource
    private OpportunityDao opportunityDao;

    @Resource
    private OpportunityQuery opportunityQuery;

    @Resource
    private OpportunityForm opportunityForm;

    @Override
    public OpportunityDao getEntityDao() {
        return opportunityDao;
    }

    @Override
    public OpportunityQuery getEntityQuery() {
        return opportunityQuery;
    }

    @Override
    public OpportunityForm getEntityForm() {
        return opportunityForm;
    }

    @Override
    public void configureFields(DisplayFields displayFields) {
        displayFields.setPropertyIds(new String[]{
                "name",
                "account.name",
                "salesStage",
                "expectedCloseDate",
                "amount",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("name", "Name");
        displayFields.setLabel("account.name", "Account");
    }
}
