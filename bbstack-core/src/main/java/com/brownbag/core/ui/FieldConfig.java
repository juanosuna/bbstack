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

package com.brownbag.core.ui;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:10 PM
 */
public class FieldConfig {
    private String propertyId;
    private String label;
    private Integer columnStart;
    private Integer rowStart;
    private Integer columnEnd;
    private Integer rowEnd;

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getColumnStart() {
        return columnStart;
    }

    public void setColumnStart(Integer columnStart) {
        this.columnStart = columnStart;
    }

    public Integer getRowStart() {
        return rowStart;
    }

    public void setRowStart(Integer rowStart) {
        this.rowStart = rowStart;
    }

    public Integer getColumnEnd() {
        return columnEnd;
    }

    public void setColumnEnd(Integer columnEnd) {
        this.columnEnd = columnEnd;
    }

    public Integer getRowEnd() {
        return rowEnd;
    }

    public void setRowEnd(Integer rowEnd) {
        this.rowEnd = rowEnd;
    }
}
