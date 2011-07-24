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

import com.brownbag.sample.entity.*;
import com.brownbag.sample.view.contact.ContactQuery;
import com.google.i18n.phonenumbers.NumberParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

public class ContactDaoTest extends AbstractDomainTest {

    @Resource
    private ContactDao contactDao;

    @Resource
    private AddressDao addressDao;

    @Resource
    private StateDao stateDao;

    @Resource
    private CountryDao countryDao;

    @Resource
    private ContactQuery contactQuery;

//    @Before
    public void setup() throws NumberParseException {

        Country country = new Country("MX", "Mexico");
        countryDao.persist(country);
        State state = new State("YU", "Yucatan", country);
        stateDao.persist(state);

        Contact contact = new Contact();
        contact.setFirstName("Juan");
        contact.setLastName("Osuna");
        contact.setMainPhone(new Phone("(704) 555-1212", "US"));
        contact.getMainPhone().setType(PhoneType.BUSINESS);
        contact.setBirthDate(TestInitializer.createBirthDate());

        Address address = new Address();
        address.setStreet("100 Main St.");
        address.setCity("Merida");
        address.setState(state);
        address.setCountry(country);
        addressDao.persist(address);
        contact.setAddress(address);
        contact.setOtherAddress(null);
        contactDao.persist(contact);
    }

    @Test
    public void findByName() {
        contactQuery.setLastName("Osuna");
        List<Contact> contacts = contactQuery.execute();
        Assert.assertNotNull(contacts);
        Assert.assertTrue(contacts.size() > 0);
        Assert.assertEquals("Osuna", contacts.get(0).getLastName());
    }

    @Test
    public void orderByStateCode() {
        List<Contact> contacts = contactDao.orderByStateCode();
        Assert.assertNotNull(contacts);
        Assert.assertTrue(contacts.size() > 0);
    }

}
