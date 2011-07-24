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
import com.brownbag.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.view.account.related.RelatedContacts;
import com.brownbag.sample.view.account.related.RelatedOpportunities;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;
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
public class AccountForm extends EntityForm<Account> {

    @Resource
    private StateDao stateDao;

    @Resource
    private RelatedContacts relatedContacts;

    @Resource
    private RelatedOpportunities relatedOpportunities;

    @Override
    public List<ToManyRelationship> getToManyRelationships() {
        List<ToManyRelationship> toManyRelationships = new ArrayList<ToManyRelationship>();
        toManyRelationships.add(relatedContacts);
        toManyRelationships.add(relatedOpportunities);

        return toManyRelationships;
    }

    @Override
    public void configureFields(FormFields formFields) {

        formFields.setPosition("Overview", "name", 0, 0);
        formFields.setPosition("Overview", "types", 0, 1);
        formFields.setPosition("Overview", "numberOfEmployees", 1, 0);
        formFields.setPosition("Overview", "annualRevenue", 1, 1);

        formFields.setPosition("Address", "address.street", 0, 0);
        formFields.setPosition("Address", "address.city", 0, 1);
        formFields.setPosition("Address", "address.country", 1, 0);
        formFields.setPosition("Address", "address.zipCode", 1, 1);
        formFields.setPosition("Address", "address.state", 2, 0);

        formFields.setMultiSelectDimensions("types", 3, 10);

        formFields.setSelectItems("address.state", new ArrayList());
        formFields.addValueChangeListener("address.country", this, "countryChanged");
    }

    public void countryChanged(Property.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        List<State> states = stateDao.findByCountry(newCountry);

        FormFields formFields = getFormFields();
        formFields.setVisible("address.state", !states.isEmpty());
        formFields.setSelectItems("address.state", states);

        if (newCountry != null && newCountry.getMinPostalCode() != null && newCountry.getMaxPostalCode() != null) {
            formFields.setDescription("address.zipCode",
                    "<strong><img src=\"/sample/VAADIN/themes/customTheme/icons/comment_yellow.gif\"/> " +
                            "Postal code range:</strong>" +
                            "<ul>" +
                            "  <li>" + newCountry.getMinPostalCode() + " - " + newCountry.getMaxPostalCode() + "</li>" +
                            "</ul>");
        } else {
            formFields.setDescription("address.zipCode", null);
        }
    }

    @Override
    public String getEntityCaption() {
        return "Account Form";
    }

    @Override
    public void configurePopupWindow(Window popupWindow) {
        popupWindow.setWidth(60, Sizeable.UNITS_EM);
        popupWindow.setHeight(50, Sizeable.UNITS_EM);
    }
}
