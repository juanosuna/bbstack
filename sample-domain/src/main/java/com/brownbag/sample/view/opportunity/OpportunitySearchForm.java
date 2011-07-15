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

import com.brownbag.core.view.entity.SearchForm;
import com.brownbag.core.view.entity.field.FormFields;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@Component
@Scope("prototype")
public class OpportunitySearchForm extends SearchForm<OpportunityQuery> {

    @Override
    public String getEntityCaption() {
        return "Opportunity Search Form";
    }

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("accountName", 0, 0);
        formFields.setPosition("salesStages", 0, 1);

        formFields.getFormField("salesStages").setMultiSelectDimensions(3, 10);
    }
}
