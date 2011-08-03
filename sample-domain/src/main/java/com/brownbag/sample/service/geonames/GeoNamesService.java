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

package com.brownbag.sample.service.geonames;

import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.Currency;
import com.brownbag.sample.service.RestClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Service
public class GeoNamesService extends RestClientService {

    @Resource
    private GeoNamesClient geoNamesClient;

    public Map<String, Country> getCountries() {

        Map<String, Country> countries = new HashMap<String, Country>();

        GeoNamesResponse geoNamesResponse = geoNamesClient.getPostalCodeCountryInfo("josuna");
        for (GeoNamesResponse.CountryInfo info : geoNamesResponse.countries) {
            Country country = new Country(info.countryCode);
            country.setMinPostalCode(info.minPostalCode);
            country.setMaxPostalCode(info.maxPostalCode);
            countries.put(country.getId(), country);
        }

        return countries;
    }

    public Map<String, Currency> getCurrencies() {

        Map<String, Currency> currencies = new HashMap<String, Currency>();

        GeoNamesResponse geoNamesResponse = geoNamesClient.getCountryInfo("josuna");
        for (GeoNamesResponse.CountryInfo info : geoNamesResponse.countries) {
            Currency currency = new Currency(info.currencyCode);
            currencies.put(info.countryCode, currency);
        }

        return currencies;
    }

    @Bean
    GeoNamesClient getGeoNamesClient() throws Exception {
        return create("http://api.geonames.org", GeoNamesClient.class);
    }

    @Path("/")
    static interface GeoNamesClient {
        @Path("/postalCodeCountryInfo")
        @GET
        @Produces("application/xml")
        GeoNamesResponse getPostalCodeCountryInfo(@QueryParam("username") String username);

        @Path("/countryInfo")
        @GET
        @Produces("application/xml")
        GeoNamesResponse getCountryInfo(@QueryParam("username") String username);
    }

    @XmlRootElement(namespace = "", name = "geonames")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class GeoNamesResponse {

        @XmlElement(name = "country")
        List<CountryInfo> countries;

        @XmlAccessorType(XmlAccessType.FIELD)
        static class CountryInfo {

            public String countryCode;
            public String minPostalCode;
            public String maxPostalCode;
            public String currencyCode;
        }
    }
}
