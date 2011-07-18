package com.brownbag.sample.geoplanet;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Juan
 * Date: 7/16/11
 */

@XmlRootElement(namespace = "", name="query")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoPlanetResponse {

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

