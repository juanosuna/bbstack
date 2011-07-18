package com.brownbag.sample.geonames;

import com.brownbag.sample.geoplanet.GeoPlanetResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * User: Juan
 * Date: 7/16/11
 */
@Path("/")
public interface GeoNamesRestClient {
    @Path("/postalCodeCountryInfo")
    @GET
    @Produces("application/xml")
    GeoNamesResponse getPostalCodeCountryInfo(@QueryParam("username") String username);
}
