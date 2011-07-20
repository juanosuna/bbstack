/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
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

package com.brownbag.sample.entity;


import com.brownbag.core.entity.ReferenceEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.brownbag.core.entity.ReferenceEntity.CACHE_REGION;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = CACHE_REGION)
public class Country extends ReferenceEntity {

    private String type;
    private String minPostalCode;
    private String maxPostalCode;

    public Country() {
    }

    public Country(String id) {
        super(id);
    }

    public Country(String id, String name) {
        super(id, name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMinPostalCode() {
        return minPostalCode;
    }

    public void setMinPostalCode(String minPostalCode) {
        this.minPostalCode = minPostalCode;
    }

    public String getMaxPostalCode() {
        return maxPostalCode;
    }

    public void setMaxPostalCode(String maxPostalCode) {
        this.maxPostalCode = maxPostalCode;
    }

    public boolean isZipCodeValid(String zipCode) {
        if (getMinPostalCode() != null) {
            String minRegex = "^";
            char[] chars = getMinPostalCode().toCharArray();
            for (Character aChar : chars) {
                if (aChar.toString().matches("\\d")) {
                    minRegex += "\\d";
                } else if (aChar.toString().matches("\\w")) {
                    minRegex += "\\w";
                } else if (aChar.toString().matches("\\s")) {
                    minRegex += "\\s";
                } else {
                    minRegex += aChar;
                }
            }
            minRegex += "$";

            if (!zipCode.matches(minRegex) || zipCode.compareTo(getMinPostalCode()) < 0) {
                return false;
            }
        }

        if (getMaxPostalCode() != null) {
            String maxRegex = "^";
            char[] chars = getMaxPostalCode().toCharArray();
            for (Character aChar : chars) {
                if (aChar.toString().matches("\\d")) {
                    maxRegex += "\\d";
                } else if (aChar.toString().matches("\\w")) {
                    maxRegex += "\\w";
                } else if (aChar.toString().matches("\\s")) {
                    maxRegex += "\\s";
                } else {
                    maxRegex += aChar;
                }
            }
            maxRegex += "$";

            if (!zipCode.matches(maxRegex) || zipCode.compareTo(getMaxPostalCode()) > 0) {
                return false;
            }
        }

        return true;
    }
}
