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

/**
 * User: Juan
 * Date: 7/18/11
 */
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
