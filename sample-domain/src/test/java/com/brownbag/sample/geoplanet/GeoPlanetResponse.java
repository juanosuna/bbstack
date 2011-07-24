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

import javax.xml.bind.annotation.*;
import java.util.List;

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

