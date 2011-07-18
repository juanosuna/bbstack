package com.brownbag.sample.geoplanet;

import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Juan
 * Date: 7/18/11
 */
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
