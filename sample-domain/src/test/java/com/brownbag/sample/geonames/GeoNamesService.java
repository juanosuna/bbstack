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

package com.brownbag.sample.geonames;

import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.geoplanet.GeoPlanetResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class GeoNamesService {

    @Resource
    private GeoNamesRestClient geoNamesRestClient;

    public Map<String, Country> getCountries() {

        Map<String, Country> countries = new HashMap<String, Country>();

        GeoNamesResponse geoNamesResponse = geoNamesRestClient.getPostalCodeCountryInfo("josuna");
        for (GeoNamesResponse.CountryPostalCodeRange postalCodeRange : geoNamesResponse.countries) {
            Country country = new Country(postalCodeRange.countryCode);
            country.setMinPostalCode(postalCodeRange.minPostalCode);
            country.setMaxPostalCode(postalCodeRange.maxPostalCode);
            countries.put(country.getId(), country);
        }

        return countries;
    }
}
