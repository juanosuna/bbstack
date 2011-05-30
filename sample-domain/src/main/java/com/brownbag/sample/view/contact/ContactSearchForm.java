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

import com.brownbag.core.view.entity.EntitySearchForm;
import com.brownbag.core.view.entity.field.FormFields;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@Component
@Scope("session")
public class ContactSearchForm extends EntitySearchForm<ContactQuery> {

    @Resource
    private StateDao stateDao;

    @Override
    public String getEntityCaption() {
        return "Contact Search Form";
    }

    @Override
    public void configureFormFields(FormFields formFields) {

        formFields.setPosition("lastName", 0, 0);
        formFields.setPosition("state", 1, 0);
        formFields.setPosition("country", 2, 0);

        Select stateField = (Select) formFields.getFormField("state").getField();
        stateField.getContainerDataSource().removeAllItems();

        formFields.addValueChangeListener("country", this, "countryChanged");
    }

    public void countryChanged(Field.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        Select stateCombo = (Select) getFormFields().getFormField("state").getField();
        State selectedState = (State) stateCombo.getValue();
        BeanItemContainer<State> stateContainer = (BeanItemContainer<State>) stateCombo.getContainerDataSource();
        stateContainer.removeAllItems();
        List<State> states = stateDao.findByCountry(newCountry);
        stateContainer.addAll(states);
        if (newCountry != null && selectedState != null && !newCountry.equals(selectedState.getCountry())) {
            stateCombo.select(stateCombo.getNullSelectionItemId());
        }
    }
}
