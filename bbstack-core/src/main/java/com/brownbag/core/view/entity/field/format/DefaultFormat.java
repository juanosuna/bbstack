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

package com.brownbag.core.view.entity.field.format;

import org.springframework.stereotype.Component;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

@Component
public class DefaultFormat {

    private Format numberFormat = NumberFormat.getNumberInstance();
    private Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Format dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Format getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(Format numberFormat) {
        this.numberFormat = numberFormat;
    }

    public Format getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(Format dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Format getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(Format dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }
}
