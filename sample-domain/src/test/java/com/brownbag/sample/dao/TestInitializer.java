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
import org.springframework.beans.factory.annotation.Autowired;
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
    private AccountTypeDao accountTypeDao;

    @Autowired
    private SalesStageDao salesStageDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private OpportunityDao opportunityDao;

    @Autowired
    private CurrencyDao currencyDao;

    //    @IfProfileValue(name="initDB", value="true")
    @Test
    public void initialize() throws Exception {
        initializeAccountTypes();
        initializeSalesStages();
        initializeCurrencies();
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
            account.setCurrency(new Currency("USD"));
        } else {
            address.setCity("Toronto");
            Country country = new Country("CA");
            address.setCountry(country);
            address.setState(new State("ON"));
            address.setZipCode("M4B 1B4");
            account.setCurrency(new Currency("CAD"));
        }
        account.setAddress(address);

        account.addType(new AccountType("Investor"));
        account.addType(new AccountType("Customer"));
        account.setNumberOfEmployees(1000);
        account.setAnnualRevenue(1000000);

        accountDao.persist(account);

        initializeOpportunity(account, i);
    }

    private void initializeOpportunity(Account account, int i) {
        Opportunity opportunity = new Opportunity();
        opportunity.setName("opportunityName" + i);
        opportunity.setAccount(account);

        if (i % 2 == 0) {
            SalesStage salesStage = new SalesStage("Prospecting");
            opportunity.setSalesStage(salesStage);
            opportunity.setCurrency(new Currency("USD"));
        } else {
            SalesStage salesStage = new SalesStage("Needs Analysis");
            opportunity.setSalesStage(salesStage);
            opportunity.setCurrency(new Currency("CAD"));
        }
        opportunity.setExpectedCloseDate(new Date());
        opportunity.setProbability(.2f);
        opportunity.setCommission(.05f);

        opportunity.setAmount(1000);

        opportunityDao.persist(opportunity);
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

    private void initializeCurrencies() {
        Currency currency = new Currency("CAD", "Canadian Dollar");
        currencyDao.persist(currency);

        currency = new Currency("USD", "US Dollar");
        currencyDao.persist(currency);
    }

    private void initializeAccountTypes() {
        AccountType accountType = new AccountType("Analyst");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Competitor");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Customer");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Integrator");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Investor");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Partner");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Prospect");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Reseller");
        accountTypeDao.persist(accountType);

        accountType = new AccountType("Press");
        accountTypeDao.persist(accountType);
    }

    private void initializeSalesStages() {
        SalesStage salesStage = new SalesStage("Prospecting");
        salesStageDao.persist(salesStage);

        salesStage = new SalesStage("Qualification");
        salesStageDao.persist(salesStage);

        salesStage = new SalesStage("Needs Analysis");
        salesStageDao.persist(salesStage);

        salesStage = new SalesStage("Value Proposition");
        salesStageDao.persist(salesStage);
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
