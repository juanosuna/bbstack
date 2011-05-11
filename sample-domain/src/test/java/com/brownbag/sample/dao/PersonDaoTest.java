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

package com.brownbag.sample.dao;

import com.brownbag.sample.entity.Address;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.Person;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.query.PersonQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PersonDaoTest extends AbstractDomainTest {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CountryDao countryDao;

    @Before
    public void setup() {

        Country country = new Country("MX", "Mexico");
        countryDao.persist(country);
        State state = new State("YU", "Yucatan", country);
        stateDao.persist(state);

        Person person = new Person();
        person.setFirstName("Juan");
        person.setLastName("Osuna");
        person.setSocialSecurityNumber("123456789");
        person.setBirthDate(TestInitializer.createBirthDate());

        Address address = new Address();
        address.setStreet("100 Main St.");
        address.setCity("Merida");
        address.setState(state);
        address.setCountry(country);
        addressDao.persist(address);
        person.setAddress(address);

        personDao.persist(person);
    }

    @Test
    public void findByName() {
        PersonQuery personQuery = new PersonQuery();
        personQuery.setLastName("Osuna");
        List<Person> persons = personDao.find(personQuery);
        Assert.assertNotNull(persons);
        Assert.assertTrue(persons.size() > 0);
        Assert.assertEquals("Osuna", persons.get(0).getLastName());
    }

}
