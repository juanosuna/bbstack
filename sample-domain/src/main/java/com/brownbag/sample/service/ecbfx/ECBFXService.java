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
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.service.ecbfx;

import com.brownbag.sample.service.RestClientService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Service
public class EcbfxService extends RestClientService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Date rateDay;
    private Date fetchDay;
    private Map<String, BigDecimal> rates;

    @Resource
    private ECBFXClient ecbfxClient;

    public BigDecimal convert(BigDecimal amount, String sourceCurrencyCode, String targetCurrencyCode) {
        Map<String, BigDecimal> rates = getFXRates();
        BigDecimal sourceRate = rates.get(sourceCurrencyCode);
        if (sourceRate == null) return null;

        BigDecimal euros = amount.divide(sourceRate, 3, BigDecimal.ROUND_HALF_EVEN);
        BigDecimal targetRate = rates.get(targetCurrencyCode);
        if (targetRate == null) return null;

        return euros.multiply(targetRate).round(new MathContext(2, RoundingMode.HALF_EVEN));
    }

    public Map<String, BigDecimal> getFXRates() {
        if (rateDay == null || (DateUtils.truncatedCompareTo(rateDay, new Date(), Calendar.DAY_OF_MONTH) < 0
                &&  DateUtils.truncatedCompareTo(fetchDay, new Date(), Calendar.DAY_OF_MONTH) < 0)) {
            fetchFXRates();
        }

        return rates;
    }

    private void fetchFXRates() {
        rates = new HashMap<String, BigDecimal>();

        ECBFXResponse ecbfxResponse = ecbfxClient.getFXRates();
        try {
            rateDay = DATE_FORMAT.parse(ecbfxResponse.mainCube.quoteDate.time);
            fetchDay = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        for (ECBFXResponse.MainCube.QuoteDate.Rate rate : ecbfxResponse.mainCube.quoteDate.rates) {
            rates.put(rate.currency, new BigDecimal(rate.rate));
        }
        rates.put("EUR", new BigDecimal(1));
    }

    @Bean
    ECBFXClient getEcbfxClient() throws Exception {
        return create("http://www.ecb.int/stats", ECBFXClient.class);
    }

    @Path("/eurofxref/eurofxref-daily.xml")
    static interface ECBFXClient {
        @GET
        @Produces("application/xml")
        ECBFXResponse getFXRates();
    }

    private static final String NAMESPACE = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref";

    @XmlRootElement(namespace = "http://www.gesmes.org/xml/2002-08-01", name = "Envelope")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ECBFXResponse {

        @XmlElement(name = "Cube", namespace = NAMESPACE)
        public MainCube mainCube;

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class MainCube {

            @XmlElement(name = "Cube", namespace = NAMESPACE)
            public QuoteDate quoteDate;

            @XmlAccessorType(XmlAccessType.FIELD)
            public static class QuoteDate {

                @XmlAttribute
                public String time;

                @XmlElement(name = "Cube", namespace = NAMESPACE)
                public List<Rate> rates;

                @XmlAccessorType(XmlAccessType.FIELD)
                public static class Rate {

                    @XmlAttribute
                    public String currency;

                    @XmlAttribute
                    public String rate;
                }
            }
        }
    }
}
