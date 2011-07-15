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

import com.brownbag.core.util.BeanPropertyType;
import com.brownbag.core.util.MethodDelegate;
import com.brownbag.core.util.StringUtil;
import com.brownbag.core.view.entity.EntityForm;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.Format;

/**
 * User: Juan
 * Date: 5/10/11
 * Time: 11:10 PM
 */
public class DisplayField {
    private DisplayFields displayFields;
    private String propertyId;
    private FormLink formLink;
    private String label;
    private BeanPropertyType beanPropertyType;
    private Format format;
    private boolean isSortable = true;
    private MethodDelegate link;

    public DisplayField(DisplayFields displayFields, String propertyId) {
        this.displayFields = displayFields;
        this.propertyId = propertyId;
        beanPropertyType = BeanPropertyType.getBeanPropertyType(getDisplayFields().getEntityType(), getPropertyId());
    }

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public Class getPropertyType() {
        return beanPropertyType.getType();
    }

    public Class getCollectionValueType() {
        return beanPropertyType.getCollectionValueType();
    }

    public boolean isCollectionType() {
        return beanPropertyType.isCollectionType();
    }

    public boolean hasAnnotation(Class annotationClass) {
        return beanPropertyType.hasAnnotation(annotationClass);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return beanPropertyType.getAnnotation(annotationClass);
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isSortable() {
        return isSortable;
    }

    public void setSortable(boolean sortable) {
        isSortable = sortable;
    }

    public String getLabel() {
        if (label == null) {
            label = generateLabel();
        }

        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String generateLabel() {
        String label = getLabelFromMessageSource();
        if (label == null) {
            label = getLabelFromAnnotation();
        }
        if (label == null) {
            label = getLabelFromCode();
        }

        return label;
    }

    private String getLabelFromMessageSource() {
        String fullPropertyPath = displayFields.getEntityType().getName() + "." + propertyId;
        return displayFields.getMessageSource().getMessage(fullPropertyPath);
    }

    private String getLabelFromAnnotation() {
        BeanPropertyType beanPropertyType = com.brownbag.core.util.BeanPropertyType.getBeanPropertyType(displayFields.getEntityType(), propertyId);
        Class propertyContainerType = beanPropertyType.getContainerType();
        String propertyIdRelativeToContainerType = beanPropertyType.getId();
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(propertyContainerType,
                propertyIdRelativeToContainerType);
        Method method = descriptor.getReadMethod();
        Label labelAnnotation = method.getAnnotation(Label.class);
        if (labelAnnotation == null) {
            return null;
        } else {
            return labelAnnotation.value();
        }
    }

    private String getLabelFromCode() {
        String afterPeriod = StringUtil.extractAfterPeriod(propertyId);
        return StringUtil.humanizeCamelCase(afterPeriod);
    }

    public void setFormLink(String propertyId, EntityForm entityForm) {
        formLink = new FormLink(propertyId, entityForm);
    }

    public FormLink getFormLink() {
        return formLink;
    }

    public static class FormLink {
        private String propertyId;
        private EntityForm entityForm;

        private FormLink(String propertyId, EntityForm entityForm) {
            this.propertyId = propertyId;
            this.entityForm = entityForm;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public EntityForm getEntityForm() {
            return entityForm;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayField)) return false;

        DisplayField that = (DisplayField) o;

        if (!propertyId.equals(that.propertyId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return propertyId.hashCode();
    }

    @Override
    public String toString() {
        return "EntityField{" +
                "propertyId='" + propertyId + '\'' +
                '}';
    }
}
