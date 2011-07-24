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

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(namespace = "", name="geonames")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoNamesResponse {

    @XmlElement(name = "country")
    public List<CountryPostalCodeRange> countries;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CountryPostalCodeRange {

        public String countryCode;
        public String minPostalCode;
        public String maxPostalCode;
    }
}

