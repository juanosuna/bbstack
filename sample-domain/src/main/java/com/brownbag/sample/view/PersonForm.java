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

package com.brownbag.sample.view;

import com.brownbag.core.ui.EntityFieldFactory;
import com.brownbag.core.ui.EntityForm;
import com.brownbag.core.ui.FormConfig;
import com.brownbag.sample.dao.StateDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.Person;
import com.brownbag.sample.entity.State;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 7:52 PM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonForm extends EntityForm<Person> {

    @Resource(name = "personFormConfig")
    private FormConfig formConfig;

    private PersonFieldFactory personFieldFactory;

    @Autowired
    private StateDao stateDao;

    @Override
    public FormConfig getFormConfig() {
        return formConfig;
    }

    @Override
    public void init() {
        setCaption("Person Form");
        personFieldFactory = new PersonFieldFactory(formConfig, true);
        personFieldFactory.addValueChangeListener("address.country", this, "countryChanged");
        setFormFieldFactory(personFieldFactory);
    }

    public void countryChanged(Field.ValueChangeEvent event) {
        Country newCountry = (Country) event.getProperty().getValue();
        ComboBox stateCombo = (ComboBox) personFieldFactory.getField("address.state");
        State selectedState = (State) stateCombo.getValue();
        BeanItemContainer<State> stateContainer = (BeanItemContainer<State>) stateCombo.getContainerDataSource();
        stateContainer.removeAllItems();
        List<State> states = stateDao.findByCountry(newCountry);
        for (State state : states) {
            stateContainer.addBean(state);
        }
        if (newCountry != null && selectedState != null && !newCountry.equals(selectedState.getCountry())) {
            stateCombo.select(stateCombo.getNullSelectionItemId());
        }
    }

    public class PersonFieldFactory extends EntityFieldFactory<Person> {

        public PersonFieldFactory(FormConfig formConfig, Boolean attachValidators) {
            super(formConfig, attachValidators);
        }

        @Override
        protected Field createCustomField(Item item, Object propertyId, Component uiContext) {
            if ("state".equals(propertyId)) {
                return createReferenceCombo(propertyId, new ArrayList());
            } else {
                return null;
            }
        }
    }
}
