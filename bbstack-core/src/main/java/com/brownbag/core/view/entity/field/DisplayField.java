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

package com.brownbag.core.view.entity.field;

import com.brownbag.core.util.BeanPropertyType;
import com.brownbag.core.util.StringUtil;
import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.format.DefaultFormat;
import com.brownbag.core.view.entity.field.format.TextFormat;
import com.vaadin.data.util.PropertyFormatter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.Format;
import java.util.Date;

public class DisplayField {
    private DisplayFields displayFields;

    private String propertyId;
    private BeanPropertyType beanPropertyType;
    private FormLink formLink;
    private Format format;
    private Format defaultFormat;
    private boolean isSortable = true;
    private String columnHeader;

    public DisplayField(DisplayFields displayFields, String propertyId) {
        this.displayFields = displayFields;
        this.propertyId = propertyId;
        beanPropertyType = BeanPropertyType.getBeanPropertyType(getDisplayFields().getEntityType(), getPropertyId());
        defaultFormat = createDefaultFormat();
    }

    public DisplayFields getDisplayFields() {
        return displayFields;
    }

    public String getPropertyId() {
        return propertyId;
    }

    protected BeanPropertyType getBeanPropertyType() {
        return beanPropertyType;
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
        if (format == null) {
            return defaultFormat;
        } else {
            return format;
        }
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Format createDefaultFormat() {
        DefaultFormat defaultFormat = getDisplayFields().getDefaultFormat();

        if (getBeanPropertyType().getType() == Date.class) {
            Temporal temporal = getAnnotation(Temporal.class);
            if (temporal != null && temporal.value().equals(TemporalType.DATE)) {
                format = defaultFormat.getDateFormat();
            } else {
                format = defaultFormat.getDateTimeFormat();
            }
        } else if (Number.class.isAssignableFrom(getBeanPropertyType().getType())) {
            format = defaultFormat.getNumberFormat();
        }

        return null;
    }

    public PropertyFormatter createPropertyFormatter() {
        if (getFormat() == null) {
            return null;
        } else {
            return new TextFormat(getFormat());
        }
    }

    public boolean isSortable() {
        return isSortable;
    }

    public void setSortable(boolean sortable) {
        isSortable = sortable;
    }

    public String getLabel() {
        if (columnHeader == null) {
            columnHeader = generateLabelText();
        }

        return columnHeader;
    }

    public void setLabel(String columnHeader) {
        this.columnHeader = columnHeader;
    }

    protected String generateLabelText() {
        String labelText = getLabelTextFromMessageSource();
        if (labelText == null) {
            labelText = getLabelTextFromAnnotation();
        }
        if (labelText == null) {
            labelText = getLabelTextFromCode();
        }

        return labelText;
    }

    private String getLabelTextFromMessageSource() {
        String fullPropertyPath = displayFields.getEntityType().getName() + "." + propertyId;
        return displayFields.getMessageSource().getMessage(fullPropertyPath);
    }

    private String getLabelTextFromAnnotation() {
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

    private String getLabelTextFromCode() {
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
