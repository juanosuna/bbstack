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

package com.brownbag.core.view.entity.field;

import com.brownbag.core.util.CollectionsUtil;
import com.brownbag.core.util.MessageSource;

import java.util.*;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:15 PM
 */
public class DisplayFields {

    private Class entityType;
    private MessageSource messageSource;
    private Map<String, DisplayField> fields = new LinkedHashMap<String, DisplayField>();

    public DisplayFields(Class entityType, MessageSource messageSource) {
        this.entityType = entityType;
        this.messageSource = messageSource;
    }

    public void setPropertyIds(String[] propertyIds) {
        for (String propertyId : propertyIds) {
            DisplayField displayField = createField(propertyId);
            fields.put(propertyId, displayField);
        }
    }

    public Class getEntityType() {
        return entityType;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public List<String> getPropertyIds() {
        return new ArrayList(fields.keySet());
    }

    public String[] getPropertyIdsAsArray() {
        return CollectionsUtil.toStringArray(getPropertyIds());
    }

    public List<String> getLabels() {
        List<String> labels = new ArrayList<String>();
        List<String> propertyIds = getPropertyIds();
        for (String propertyId : propertyIds) {
            labels.add(getField(propertyId).getLabel());
        }

        return labels;
    }

    public String[] getLabelsAsArray() {
        return CollectionsUtil.toStringArray(getLabels());
    }

    public boolean containsPropertyId(String propertyId) {
        return fields.containsKey(propertyId);
    }

    public DisplayField getField(String propertyId) {
        if (!containsPropertyId(propertyId)) {
            DisplayField displayField = createField(propertyId);
            fields.put(propertyId, displayField);
        }

        return fields.get(propertyId);
    }

    protected DisplayField createField(String propertyId) {
        return new DisplayField(this, propertyId);
    }

    public Collection<DisplayField> getFields() {
        return fields.values();
    }
}
