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
import com.brownbag.core.view.entity.field.FormField;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.field.SelectField;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.*;
import com.brownbag.sample.view.select.AccountSelect;
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
public class ContactForm extends EntityForm<Contact> {

    @Resource
    private StateDao stateDao;

    @Resource
    private AccountSelect accountSelect;

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("Overview", "firstName", 0, 0);
        formFields.setPosition("Overview", "lastName", 0, 1);
        formFields.setPosition("Overview", "birthDate", 1, 0);
        formFields.setPosition("Overview", "socialSecurityNumber", 1, 1);
        formFields.setPosition("Overview", "account.name", 2, 0);
        formFields.setPosition("Overview", "doNotCall", 2, 1);

        formFields.setPosition("Primary Address", "address.street", 0, 0);
        formFields.setPosition("Primary Address", "address.city", 0, 1);
        formFields.setPosition("Primary Address", "address.country", 1, 0);
        formFields.setPosition("Primary Address", "address.zipCode", 1, 1);
        formFields.setPosition("Primary Address", "address.state", 2, 0);

        formFields.setPosition("Other Address", "otherAddress.street", 0, 0);
        formFields.setPosition("Other Address", "otherAddress.city", 0, 1);
        formFields.setPosition("Other Address", "otherAddress.country", 1, 0);
        formFields.setPosition("Other Address", "otherAddress.zipCode", 1, 1);
        formFields.setPosition("Other Address", "otherAddress.state", 2, 0);
        formFields.setTabOptional("Other Address", this, "addOtherAddress", this, "removeOtherAddress");

        formFields.setPosition("Note", "note", 0, 0);

        formFields.setLabel("account.name", "Account");

        formFields.setSelectItems("address.state", new ArrayList());
        formFields.addValueChangeListener("address.country", this, "countryChanged");

        formFields.setSelectItems("otherAddress.state", new ArrayList());
        formFields.addValueChangeListener("otherAddress.country", this, "otherCountryChanged");

        SelectField selectField = new SelectField(this, "account", accountSelect, "account.name");
        formFields.setField("account.name", selectField);
    }

    public void addOtherAddress() {
        getEntity().setOtherAddress(new Address(AddressType.OTHER));
    }

    public void removeOtherAddress() {
        getEntity().setOtherAddress(null);
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "address.state");
    }

    public void otherCountryChanged(Property.ValueChangeEvent event) {
        countryChangedImpl(event, "otherAddress.state");
    }

    public void countryChangedImpl(Property.ValueChangeEvent event, String propertyId) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);
        FormField stateField = getFormFields().getFormField(propertyId);
        stateField.setVisible(!states.isEmpty());
        stateField.setRequired(!states.isEmpty());
        stateField.setSelectItems(states);
    }

    @Override
    public String getEntityCaption() {
        return "Contact Form";
    }
}
