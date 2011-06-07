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

import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.manyselect.ManySelect;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.view.account.contactmanyselect.ContactManySelect;
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
@Scope("session")
public class AccountForm extends EntityForm<Account> {

    @Resource
    private StateDao stateDao;

    @Resource
    private ContactManySelect contactManySelect;

    @Override
    public String getEntityCaption() {
        return "Account Form";
    }

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("name", 0, 0);
        formFields.setPosition("address.street", 0, 2);
        formFields.setPosition("address.city", 1, 2);
        formFields.setPosition("address.state", 0, 3);
        formFields.setPosition("address.zipCode", 1, 3);
        formFields.setPosition("address.country", 0, 4);

        formFields.setSelectItems("address.state", new ArrayList());
        formFields.addValueChangeListener("address.country", this, "countryChanged");
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);
        getFormFields().setSelectItems("address.state", states);
    }

    @Override
    public List<ManySelect> getManySelects() {
        List<ManySelect> manySelects = new ArrayList<ManySelect>();
        manySelects.add(contactManySelect);

        return manySelects;
    }
}
