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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class TestInitializer extends AbstractDomainTest {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private AccountDao accountDao;

    //    @IfProfileValue(name="initDB", value="true")
    @Test
    public void initialize() throws Exception {

        initializeCountriesStates();
        initializeContacts();
    }

    private void initializeContacts() {
        for (Integer i = 0; i < 1000; i++) {
            Contact contact = new Contact(
                    "first" + i,
                    "last" + i,
                    i.toString()
            );
            contact.setSocialSecurityNumber("123456789");
            contact.setBirthDate(createBirthDate());

            Address address = new Address();
            address.setStreet(i + " Main St");
            if (i % 2 == 0) {
                address.setCity("Charlotte");
                Country country = new Country("US");
                address.setCountry(country);
                address.setState(new State("NC"));
                address.setZipCode("28202");
            } else {
                address.setCity("Toronto");
                Country country = new Country("CA");
                address.setCountry(country);
                address.setState(new State("ON"));
                address.setZipCode("M4B 1B4");
            }
            contact.setAddress(address);
            initializeAccount(contact, i);
            contactDao.persist(contact);
        }
    }

    private void initializeAccount(Contact contact, int i) {
        Account account = new Account();
        account.setName("companyName" + i);
        contact.setAccount(account);

        Address address = new Address();
        address.setStreet(i + " Main St");
        if (i % 2 == 0) {
            address.setCity("Charlotte");
            Country country = new Country("US");
            address.setCountry(country);
            address.setState(new State("NC"));
            address.setZipCode("28202");
        } else {
            address.setCity("Toronto");
            Country country = new Country("CA");
            address.setCountry(country);
            address.setState(new State("ON"));
            address.setZipCode("M4B 1B4");
        }
        account.setAddress(address);

        accountDao.persist(account);
    }

    private void initializeCountriesStates() {
        Country canada = new Country("CA", "Canada");
        countryDao.persist(canada);

        State state = new State("ON", "Ontario", canada);
        stateDao.persist(state);

        state = new State("QC", "Quebec", canada);
        stateDao.persist(state);

        state = new State("NS", "Nova Scotia", canada);
        stateDao.persist(state);

        state = new State("NL", "Newfoundland", canada);
        stateDao.persist(state);

        Country us = new Country("US", "United States");
        countryDao.persist(us);

        state = new State("NC", "North Carolina", us);
        stateDao.persist(state);

        state = new State("SC", "South Carolina", us);
        stateDao.persist(state);

        state = new State("VA", "Virginia", us);
        stateDao.persist(state);

        state = new State("WV", "West Virginia", us);
        stateDao.persist(state);
    }

    public static Date createBirthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.YEAR, 1950);

        return calendar.getTime();
    }
}
