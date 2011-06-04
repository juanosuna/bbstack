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

package com.brownbag.sample.view.contact;

import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.EntitySelectField;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.view.accountselect.AccountSelect;
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
public class ContactForm extends EntityForm<Contact> {

    @Resource
    private StateDao stateDao;

    @Resource
    private AccountSelect accountSelect;

    @Override
    public String getEntityCaption() {
        return "Contact Form";
    }

    @Override
    public void configureFormFields(FormFields formFields) {
        formFields.setPosition("firstName", 0, 0);
        formFields.setPosition("lastName", 1, 0);
        formFields.setPosition("birthDate", 0, 1);
        formFields.setPosition("socialSecurityNumber", 1, 1);
        formFields.setPosition("address.street", 0, 2);
        formFields.setPosition("address.city", 1, 2);
        formFields.setPosition("address.state", 0, 3);
        formFields.setPosition("address.zipCode", 1, 3);
        formFields.setPosition("address.country", 0, 4);
        formFields.setPosition("account.name", 1, 4);

        formFields.setLabel("account.name", "Account");

        formFields.setSelectItems("address.state", new ArrayList());
        formFields.addValueChangeListener("address.country", this, "countryChanged");

        EntitySelectField entitySelectField = new EntitySelectField(this, "account", accountSelect);
        formFields.setField("account.name", entitySelectField);
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);
        getFormFields().setSelectItems("address.state", states);
    }
}
