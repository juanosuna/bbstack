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

package com.brownbag.sample.query;

import com.brownbag.core.query.EntityQuery;
import com.brownbag.sample.dao.PersonDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.Person;
import com.brownbag.sample.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * User: Juan
 * Date: 2/8/11
 * Time: 12:01 AM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonQuery extends EntityQuery<Person> {

    @Autowired
    private PersonDao personDao;

    private String lastName;
    private State state;
    private Country country;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Person> execute() {
        List<Person> persons = personDao.find(this);

        return persons;
    }

    public void clear() {
        setLastName(null);
        setCountry(null);
        setState(null);
        setOrderByField(null);
        setOrderDirection(OrderDirection.ASC);
    }

    @Override
    public String toString() {
        return "PersonQuery{" +
                "lastName='" + lastName + '\'' +
                ", state=" + state +
                '}';
    }
}
