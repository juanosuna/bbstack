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

import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.field.SelectField;
import com.brownbag.sample.entity.Opportunity;
import com.brownbag.sample.view.select.AccountSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@Component
@Scope("prototype")
public class OpportunityForm extends EntityForm<Opportunity> {

    @Resource
    private AccountSelect accountSelect;

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("Overview", "name", 0, 0);
        formFields.setPosition("Overview", "account.name", 0, 1);
        formFields.setPosition("Overview", "salesStage", 0, 2);
        formFields.setPosition("Overview", "amount", 1, 0);
        formFields.setPosition("Overview", "currency", 1, 1);
        formFields.setPosition("Overview", "probability", 2, 0);
        formFields.setPosition("Overview", "commission", 2, 1);
        formFields.setPosition("Overview", "expectedCloseDate", 2, 2);

        formFields.setPosition("Description", "description", 0, 0);

        formFields.setLabel("account.name", "Account");

        SelectField selectField = new SelectField(this, "account", accountSelect);
        formFields.setField("account.name", selectField);
    }

    @Override
    public String getEntityCaption() {
        return "Opportunity Form";
    }
}
