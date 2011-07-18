package com.brownbag.sample.geonames;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * User: Juan
 * Date: 7/16/11
 */

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

