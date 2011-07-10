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
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.core.view.entity.field.SelectField;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.*;
import com.brownbag.sample.view.contact.accountsingleselect.AccountSingleSelect;
import com.vaadin.data.Property;
import com.vaadin.ui.RichTextArea;
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
    private AccountSingleSelect accountSelect;

    @Override
    public String getEntityCaption() {
        return "Contact Form";
    }

    @Override
    public void configureFields(FormFields formFields) {
        formFields.setPosition("Overview", "firstName", 0, 0);
        formFields.setPosition("Overview", "lastName", 1, 0);
        formFields.setPosition("Overview", "birthDate", 0, 1);
        formFields.setPosition("Overview", "socialSecurityNumber", 1, 1);
        formFields.setPosition("Overview", "account.name", 0, 2);
        formFields.setPosition("Overview", "doNotCall", 1, 2);

        formFields.setPosition("Physical Address", "address.street", 0, 0);
        formFields.setPosition("Physical Address", "address.city", 1, 0);
        formFields.setPosition("Physical Address", "address.country", 0, 1);
        formFields.setPosition("Physical Address", "address.zipCode", 1, 1);
        formFields.setPosition("Physical Address", "address.state", 0, 2);

        formFields.setPosition("Mailing Address", "mailingAddress.street", 0, 0);
        formFields.setPosition("Mailing Address", "mailingAddress.city", 1, 0);
        formFields.setPosition("Mailing Address", "mailingAddress.country", 0, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.zipCode", 1, 1);
        formFields.setPosition("Mailing Address", "mailingAddress.state", 0, 2);
        formFields.setTabOptional("Mailing Address", this, "addMailingAddress", this, "removeMailingAddress");

        formFields.setPosition("Note", "note", 0, 0);

        formFields.setLabel("account.name", "Account");

        formFields.setSelectItems("address.state", new ArrayList());
        formFields.addValueChangeListener("address.country", this, "countryChanged");

        formFields.setSelectItems("mailingAddress.state", new ArrayList());
        formFields.addValueChangeListener("mailingAddress.country", this, "mailingCountryChanged");

        SelectField selectField = new SelectField(this, "account", accountSelect, uiMessageSource);
        formFields.setField("account.name", selectField);

    }

    public void addMailingAddress() {
        getEntity().setMailingAddress(new Address(AddressType.MAILING));
    }

    public void removeMailingAddress() {
        getEntity().setMailingAddress(null);
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);
        getFormFields().setSelectItems("address.state", states);
    }

    public void mailingCountryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);
        getFormFields().setSelectItems("mailingAddress.state", states);
    }
}
