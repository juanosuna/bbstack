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

import com.brownbag.core.view.entity.EntityQuery;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
@Component
@Scope("session")
public class ContactQuery extends EntityQuery<Contact> {

    @Resource
    private ContactDao contactDao;

    private String lastName;
    private Set<State> states = new HashSet<State>();
    private Country country;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public List<Contact> execute() {
        if (getOrderByPropertyId() == null) {
            setOrderByPropertyId("lastModified");
            setOrderDirection(OrderDirection.DESC);
        }
        return contactDao.find(this);
    }

    @Override
    public String toString() {
        return "ContactQuery{" +
                "lastName='" + lastName + '\'' +
                ", states=" + states +
                '}';
    }
}
