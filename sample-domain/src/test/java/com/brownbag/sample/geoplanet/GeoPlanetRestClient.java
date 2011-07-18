package com.brownbag.sample.geoplanet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * User: Juan
 * Date: 7/16/11
 */
@Path("/yql")
public interface GeoPlanetRestClient {
    @GET
    @Produces("application/xml")
    GeoPlanetResponse getPlaces(@QueryParam("q") String yql);
}
