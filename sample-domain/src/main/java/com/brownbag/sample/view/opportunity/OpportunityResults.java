/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.view.opportunity;

import com.brownbag.core.view.entity.Results;
import com.brownbag.core.view.entity.ResultsTable;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.OpportunityDao;
import com.brownbag.sample.entity.Opportunity;
import com.vaadin.terminal.Sizeable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
                "salesStage",
                "expectedCloseDate",
                "amount",
                "lastModified",
                "modifiedBy"
        });

        displayFields.setLabel("name", "Name");
    }

    @Override
    public void configureTable(ResultsTable resultsTable) {
        resultsTable.setWidth(70, Sizeable.UNITS_EM);
    }
}
