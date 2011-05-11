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

import java.util.*;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:15 PM
 */
public class FormConfig {
    private int columns;
    private int rows;

    private Map<String, FieldConfig> fieldMap;

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setFieldConfigs(List<FieldConfig> fieldConfigs) {
        fieldMap = new HashMap<String, FieldConfig>();
        for (FieldConfig fieldConfig : fieldConfigs) {
            fieldMap.put(fieldConfig.getPropertyId(), fieldConfig);
        }
    }

    public Set<String> getPropertyIds() {
        return fieldMap.keySet();
    }

    public boolean containsProperty(String propertyId) {
        return fieldMap.containsKey(propertyId);
    }

    public String getLabel(String propertyId) {
        return fieldMap.get(propertyId).getLabel();
    }

    public FieldConfig getFieldConfig(String propertyId) {
        return fieldMap.get(propertyId);
    }
}
