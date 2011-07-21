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

import com.brownbag.core.view.entity.entityselect.EntitySelect;
import com.brownbag.core.view.entity.entityselect.EntitySelectResults;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.OpportunityDao;
import com.brownbag.sample.entity.Opportunity;
import com.brownbag.sample.view.opportunity.OpportunityQuery;
import com.brownbag.sample.view.opportunity.OpportunitySearchForm;
import com.vaadin.ui.Window;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class OpportunitySelect extends EntitySelect<Opportunity> {

    @Resource
    private OpportunitySearchForm opportunitySearchForm;

    @Resource
    private OpportunitySelectResults opportunitySelectResults;

    @Override
    public OpportunitySearchForm getSearchForm() {
        return opportunitySearchForm;
    }

    @Override
    public OpportunitySelectResults getResultsComponent() {
        return opportunitySelectResults;
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setHeight(null);
        popupWindow.setWidth(null);
    }

    @Component
    @Scope("prototype")
    public static class OpportunitySelectResults extends EntitySelectResults<Opportunity> {

        @Resource
        private OpportunityDao opportunityDao;

        @Resource
        private OpportunityQuery opportunityQuery;

        @Override
        public OpportunityDao getEntityDao() {
            return opportunityDao;
        }

        @Override
        public OpportunityQuery getEntityQuery() {
            return opportunityQuery;
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

            displayFields.getField("name").setLabel("Name");
            displayFields.getField("account.name").setLabel("Account");
        }
    }
}

