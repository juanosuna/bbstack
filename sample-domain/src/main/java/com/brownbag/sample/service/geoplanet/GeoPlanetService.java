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

package com.brownbag.sample.service.geoplanet;

import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.service.RestClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Service
public class GeoPlanetService extends RestClientService {

    @Resource
    private GeoPlanetClient geoPlanetClient;

    public Set<Country> getCountries() {

        Set<Country> countries = new HashSet<Country>();

        GeoPlanetResponse geoPlanetResponse = geoPlanetClient.getPlaces("select country from geo.countries where view='long'");
        for (GeoPlanetResponse.Place place : geoPlanetResponse.places) {
            Country country = new Country(place.country.code, place.country.name);
            country.setCountryType(place.country.type);
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
        GeoPlanetResponse geoPlanetResponse = geoPlanetClient.getPlaces(String.format(queryStr, countriesBuilder.toString()));
        for (GeoPlanetResponse.Place place : geoPlanetResponse.places) {
            if (!place.admin1.code.isEmpty()) {
                Country country = new Country(place.country.code);
                State state = new State(place.admin1.code, place.admin1.name, country);
                state.setStateType(place.admin1.type);
                states.add(state);
            }
        }

        return states;
    }

    @Bean
    GeoPlanetClient getGeoPlanetClient() throws Exception {
        return create("http://query.yahooapis.com/v1/public", GeoPlanetClient.class);
    }

    @Path("/yql")
    static interface GeoPlanetClient {
        @GET
        @Produces("application/xml")
        GeoPlanetResponse getPlaces(@QueryParam("q") String yql);
    }

    @XmlRootElement(namespace = "", name="query")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class GeoPlanetResponse {

        @XmlAttribute(namespace = "http://www.yahooapis.com/v1/base.rng")
        public int count;

        @XmlElementWrapper(namespace = "", name = "results")
        @XmlElement(name = "place")
        public List<Place> places;

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Place {

            public Country country;

            public Admin1 admin1;

            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Country {

                @XmlAttribute
                public String code;

                @XmlAttribute
                public String type;

                @XmlValue
                public String name;
            }

            @XmlAccessorType(XmlAccessType.FIELD)
            public static class Admin1 {

                @XmlAttribute
                public String code;

                @XmlAttribute
                public String type;

                @XmlValue
                public String name;
            }
        }
    }
}
