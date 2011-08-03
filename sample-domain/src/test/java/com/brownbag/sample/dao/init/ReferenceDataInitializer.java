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

package com.brownbag.sample.dao.init;

import com.brownbag.sample.dao.*;
import com.brownbag.sample.entity.*;
import com.brownbag.sample.entity.Currency;
import com.brownbag.sample.service.geonames.GeoNamesService;
import com.brownbag.sample.service.geoplanet.GeoPlanetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional
public class ReferenceDataInitializer {

    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static final String[] ACCOUNT_TYPES = {"Analyst", "Competitor", "Customer", "Integrator",
            "Investor", "Partner", "Press", "Prospect", "Reseller", "Other"};

    public static final String[] INDUSTRIES = {"Apparel", "Banking", "Biotechnology", "Chemicals",
            "Communication", "Construction", "Consulting", "Education", "Electronics", "Energy",
            "Engineering", "Entertainment", "Environmental", "Finance", "Food & Beverage", "Government",
            "Healthcare", "Hospitality", "Insurance", "Machinery", "Manufacturing", "Media",
            "Not for Profit", "Recreation", "Retail", "Shipping", "Technology", "Telecommunications",
            "Transportation", "Utilities", "Other"};

    public static final String[] LEAD_SOURCES = {"Cold Call", "Existing Customer", "Self-generated", "Employee",
            "Partner", "Public Relations", "Direct Mail", "Conference", "Trade Show", "Website", "Word of Mouth",
            "Other"};

    public static final String[] SALES_STAGES = {"Prospecting", "Qualification", "Needs Analysis", "Value Proposition",
            "Id. Decision Makers", "Perception Analysis", "Proposal/Price Quote", "Negotiation/Review", "Closed One",
            "Closed Lost"};

    public static Set<String> COUNTRIES_WITH_STATES = new HashSet<String>(Arrays.asList(
            "United States",
            "Canada",
            "Mexico",
            "Australia"
    ));

    @Resource
    private AccountTypeDao accountTypeDao;

    @Resource
    private IndustryDao industryDao;

    @Resource
    private LeadSourceDao leadSourceDao;

    @Resource
    private SalesStageDao salesStageDao;

    @Resource
    private StateDao stateDao;

    @Resource
    private CountryDao countryDao;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private GeoPlanetService geoPlanetService;

    @Resource
    private GeoNamesService geoNamesService;

    public AccountType randomAccountType() {
        return random(accountTypeDao.findAll());
    }

    public Industry randomIndustry() {
        return random(industryDao.findAll());
    }

    public LeadSource randomLeadSource() {
        return random(leadSourceDao.findAll());
    }

    public SalesStage randomSalesStage() {
        return random(salesStageDao.findAll());
    }

    public Currency randomCurrency() {
        return random(currencyDao.findAll());
    }

    public Country randomCountry() {
        return random(countryDao.findAll());
    }

    public State randomState() {
        return random(stateDao.findAll());
    }

    public static int random(int start, int end) {
        if (start == end) {
            return start;
        } else {
            return RANDOM.nextInt(++end - start) + start;
        }
    }

    public static <T> T random(List<T> entities) {
        return entities.get(ReferenceDataInitializer.random(0, entities.size() - 1));
    }

    public void initialize() {
        initializeReferenceEntities();
        initializeCountriesAndCurrencies();
        initializeStates();
    }

    private void initializeReferenceEntities() {
        for (String accountType : ACCOUNT_TYPES) {
            AccountType referenceEntity = new AccountType(accountType);
            accountTypeDao.persist(referenceEntity);
        }
        accountTypeDao.getEntityManager().flush();

        for (String industry : INDUSTRIES) {
            Industry referenceEntity = new Industry(industry);
            industryDao.persist(referenceEntity);
        }
        industryDao.getEntityManager().flush();

        for (int i = 0, lead_sourcesLength = LEAD_SOURCES.length; i < lead_sourcesLength; i++) {
            String leadSource = LEAD_SOURCES[i];
            LeadSource referenceEntity = new LeadSource(leadSource);
            referenceEntity.setSortOrder(i);
            leadSourceDao.persist(referenceEntity);
        }
        leadSourceDao.getEntityManager().flush();

        for (int i = 0, sales_stagesLength = SALES_STAGES.length; i < sales_stagesLength; i++) {
            String leadSource = SALES_STAGES[i];
            SalesStage referenceEntity = new SalesStage(leadSource);
            referenceEntity.setSortOrder(i);
            salesStageDao.persist(referenceEntity);
        }
        salesStageDao.getEntityManager().flush();
    }

    private void initializeCountriesAndCurrencies() {
        Set<Country> countries = geoPlanetService.getCountries();
        Map<String, Country> geoNamesCountries = geoNamesService.getCountries();
        Map<String, Currency> geoNamesCurrencies = geoNamesService.getCurrencies();
        for (Country country : countries) {
            Country geoNamesCountry = geoNamesCountries.get(country.getId());
            if (geoNamesCountry != null) {
                country.setMinPostalCode(geoNamesCountry.getMinPostalCode());
                country.setMaxPostalCode(geoNamesCountry.getMaxPostalCode());
                Currency currency = geoNamesCurrencies.get(country.getId());
                if (currency != null && !currencyDao.isPersistent(currency)) {
                    if (currency.getId().equals("EUR")) {
                        currency.setDisplayName(currency.getId() + "-Europe");
                    } else if (currency.getId().equals("EUR")) {
                        currency.setDisplayName(currency.getId() + "-United States");
                    } else {
                        currency.setDisplayName(currency.getId() + "-" + country.getDisplayName());
                    }
                    currencyDao.persist(currency);
                }
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

        stateDao.getEntityManager().flush();
    }
}
