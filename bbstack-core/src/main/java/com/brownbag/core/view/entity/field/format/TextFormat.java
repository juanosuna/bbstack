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

import com.vaadin.data.util.PropertyFormatter;

import java.text.Format;

public class TextFormat extends PropertyFormatter {

    private Format format;

    public TextFormat(Format format) {
        this.format = format;
    }

    @Override
    public String format(Object value) {
        return format.format(value);
    }

    @Override
    public Object parse(String formattedValue) throws Exception {
        return format.parseObject(formattedValue);
    }
}
