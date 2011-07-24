/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.dao;

import com.brownbag.sample.entity.*;
import com.brownbag.sample.entity.Currency;
import com.brownbag.sample.geonames.GeoNamesService;
import com.brownbag.sample.geoplanet.GeoPlanetService;
import org.junit.Test;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class TestInitializer extends AbstractDomainTest {

    public static Set<String> COUNTRIES_WITH_STATES = new HashSet<String>(Arrays.asList(
            "United States",
            "Canada",
            "Mexico",
            "Australia"
    ));

    @Resource
    private StateDao stateDao;

    @Resource
    private CountryDao countryDao;

    @Resource
    private AccountTypeDao accountTypeDao;

    @Resource
    private SalesStageDao salesStageDao;

    @Resource
    private ContactDao contactDao;

    @Resource
    private AccountDao accountDao;

    @Resource
    private OpportunityDao opportunityDao;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private GeoPlanetService geoPlanetService;

    @Resource
    private GeoNamesService geoNamesService;

    //    @IfProfileValue(name="initDB", value="true")
    @Test
    public void initialize() throws Exception {
        initializeCountries();
        initializeStates();
        initializeAccountTypes();
        initializeSalesStages();
        initializeCurrencies();
        initializeContacts();
    }

    private void initializeCountries() {
        Set<Country> countries = geoPlanetService.getCountries();
        Map<String, Country> countriesWithPostCodeRange = geoNamesService.getCountries();
        for (Country country : countries) {
            Country countryWithPostalCodeRange = countriesWithPostCodeRange.get(country.getId());
            if (countryWithPostalCodeRange != null) {
                country.setMinPostalCode(countryWithPostalCodeRange.getMinPostalCode());
                country.setMaxPostalCode(countryWithPostalCodeRange.getMaxPostalCode());
            }
            countryDao.persist(country);
        }

        countryDao.getEntityManager().flush();
    }

    private void initializeStates() {
        Set<State> states = geoPlanetService.getStates(COUNTRIES_WITH_STATES);
        for (State state : states) {
            stateDao.persist(state);
        }
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

    private void initializeContacts() {
        for (Integer i = 0; i < 1000; i++) {
            Contact contact = null;
            contact = new Contact("first" + i, "last" + i);
            contact.setBirthDate(createBirthDate());
            contact.setMainPhoneFormatted("(704) 555-1212");
            contact.getMainPhone().setType(PhoneType.BUSINESS);

            Address address = new Address();
            address.setStreet(i + " Main St");
            if (i % 2 == 0) {
                address.setCity("Charlotte");
                Country country = new Country("US");
                address.setCountry(country);
                address.setState(new State("US-NC"));
                address.setZipCode("28202");
            } else {
                address.setCity("Toronto");
                Country country = new Country("CA");
                address.setCountry(country);
                address.setState(new State("CA-ON"));
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
            address.setState(new State("US-NC"));
            address.setZipCode("28202");
            account.setCurrency(new Currency("USD"));
        } else {
            address.setCity("Toronto");
            Country country = new Country("CA");
            address.setCountry(country);
            address.setState(new State("CA-ON"));
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
