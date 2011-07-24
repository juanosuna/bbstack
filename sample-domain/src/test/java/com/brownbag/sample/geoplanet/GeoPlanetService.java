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

package com.brownbag.sample.geoplanet;

import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Service
public class GeoPlanetService {

    @Resource
    private GeoPlanetRestClient geoPlanetRestClient;

    public Set<Country> getCountries() {

        Set<Country> countries = new HashSet<Country>();

        GeoPlanetResponse geoPlanetResponse = geoPlanetRestClient.getPlaces("select country from geo.countries where view='long'");
        for (GeoPlanetResponse.Place place : geoPlanetResponse.places) {
            Country country = new Country(place.country.code, place.country.name);
            country.setType(place.country.type);
            countries.add(country);
        }

        return countries;
    }

    public Set<State> getStates(Set<String> countriesWithStates) {

        String queryStr = "select country, admin1 from geo.states where place in (%1$s) and view='long'";
        StringBuilder countriesBuilder = new StringBuilder();
        for (String countryWithStates : countriesWithStates) {
            countriesBuilder.append("'").append(countryWithStates).append("',");
        }
        countriesBuilder.deleteCharAt(countriesBuilder.length() - 1);

        Set<State> states = new HashSet<State>();
        GeoPlanetResponse geoPlanetResponse = geoPlanetRestClient.getPlaces(String.format(queryStr, countriesBuilder.toString()));
        for (GeoPlanetResponse.Place place : geoPlanetResponse.places) {
            if (!place.admin1.code.isEmpty()) {
                Country country = new Country(place.country.code);
                State state = new State(place.admin1.code, place.admin1.name, country);
                state.setType(place.admin1.type);
                states.add(state);
            }
        }

        return states;
    }
}
