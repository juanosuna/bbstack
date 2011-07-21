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

package com.brownbag.sample.view.account;

import com.brownbag.core.view.entity.SearchForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.vaadin.data.Property;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@Component
@Scope("prototype")
public class AccountSearchForm extends SearchForm<AccountQuery> {

    @Resource
    private StateDao stateDao;

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("name", 0, 0);
        formFields.setPosition("country", 0, 1);
        formFields.setPosition("states", 0, 2);

        formFields.setSelectItems("states", new ArrayList());
        formFields.setVisible("states", false);
        formFields.setMultiSelectDimensions("states", 5, 15);

        formFields.addValueChangeListener("country", this, "countryChanged");
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);

        getFormFields().setSelectItems("states", states);
        getFormFields().setVisible("states", states.size() > 0);
    }

    @Override
    public String getEntityCaption() {
        return "Account Search Form";
    }
}
