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

import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.field.SelectField;
import com.brownbag.sample.entity.Opportunity;
import com.brownbag.sample.view.select.AccountSelect;
import com.brownbag.sample.view.select.UserSelect;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class OpportunityForm extends EntityForm<Opportunity> {

    @Resource
    private AccountSelect accountSelect;

    @Resource
    private UserSelect userSelect;

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("Overview", "name", 1, 1);
        formFields.setPosition("Overview", "opportunityType", 1, 2);
        formFields.setPosition("Overview", "account.name", 1, 3);

        formFields.setPosition("Overview", "salesStage", 2, 1);
        formFields.setPosition("Overview", "leadSource", 2, 2);
        formFields.setPosition("Overview", "assignedTo.loginName", 2, 3);

        formFields.setPosition("Overview", "amount", 3, 1);
        formFields.setPosition("Overview", "currency", 3, 2);
        formFields.setPosition("Overview", "probability", 3, 3);
        formFields.setPosition("Overview", "expectedCloseDate", 3, 4);

        formFields.setPosition("Description", "description", 1, 1);

        formFields.setLabel("opportunityType", "Type");
        formFields.setLabel("account.name", "Account");
        formFields.setLabel("assignedTo.loginName", "Assigned to");

        SelectField selectField = new SelectField(this, "assignedTo", userSelect);
        formFields.setField("assignedTo.loginName", selectField);

        selectField = new SelectField(this, "account", accountSelect);
        formFields.setField("account.name", selectField);
    }

    @Override
    public String getEntityCaption() {
        return "Opportunity Form";
    }
}
