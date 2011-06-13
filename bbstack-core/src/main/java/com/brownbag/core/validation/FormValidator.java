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

package com.brownbag.core.validation;

import com.brownbag.core.util.BeanPropertyType;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.NullCapableNestedMethodProperty;
import com.vaadin.ui.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 10:36 PM
 */
@Service
@Scope("prototype")
public class FormValidator<T> {

    @Autowired
    private Validation validation;

    private Class beanType;
    private Object bean;

    private Locale locale;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        beanType = bean.getClass();
        this.bean = bean;
    }

    public FieldValidator addValidator(Field field, String propertyId) {
        FieldValidator validator = new FieldValidator(propertyId);
        field.addValidator(validator);

        if (validator.isRequired()) {
            field.setRequired(true);
            field.setRequiredError(validator.getRequiredMessage());
        }
        return validator;
    }

    /**
     * Sets the locale used for validation error messages.
     * <p/>
     * Revalidation is not automatically triggered by setting the locale.
     *
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the locale used for validation error messages.
     *
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    public class FieldValidator implements Validator {
        private String propertyId;
        private BeanPropertyType beanPropertyType;
        private NullCapableNestedMethodProperty nullCapableNestedMethodProperty;

        public FieldValidator(String propertyId) {
            this.propertyId = propertyId;
            beanPropertyType = com.brownbag.core.util.BeanPropertyType.getBeanProperty(beanType, propertyId);
            nullCapableNestedMethodProperty = new NullCapableNestedMethodProperty(bean, propertyId);
        }

        public String getPropertyId() {
            return propertyId;
        }

        public String getRequiredMessage() {
            return getErrorMessage(null, NotNull.class);
        }

        @Override
        public void validate(Object value) throws InvalidValueException {

            try {
                convertValue(value);
            } catch (Exception e) {
                String msg = getErrorMessage(value);
                if (msg != null) {
                    throw new InvalidValueException(msg);
                } else {
                    // there should be always some constraints if conversion is
                    // needed
                    // for example if String -> Integer then Digits annotation
                    throw new InvalidValueException("Conversion exception");
                }
            }

            Set<ConstraintViolation<Object>> violations = validation.validateProperty(bean, propertyId);

            List<String> exceptions = new ArrayList<String>();
            for (ConstraintViolation violation : violations) {
                String msg = violation.getMessage();
                exceptions.add(msg);
            }

            StringBuilder b = new StringBuilder();
            for (int i = 0; i < exceptions.size(); i++) {
                if (i != 0) {
                    b.append("<br/>");
                }
                b.append(exceptions.get(i));
            }
            throw new InvalidValueException(b.toString());
        }

        @Override
        public boolean isValid(Object value) {
            try {
                validate(value);
                return true;
            } catch (InvalidValueException e) {
                return false;
            }
        }

        public boolean isRequired() {
            BeanPropertyType currentBeanPropertyType = beanPropertyType;
            while (currentBeanPropertyType != null) {
                if (!isRequired(currentBeanPropertyType)) {
                    if (nullCapableNestedMethodProperty.hasNullInPropertyPath()) {

                    }
                    return false;
                } else {
                    currentBeanPropertyType = currentBeanPropertyType.getParent();
                }
            }

            return true;
        }

        private boolean isRequired(BeanPropertyType beanPropertyType) {
            PropertyDescriptor desc = validation.getConstraintsForClass(beanPropertyType.getContainerType())
                    .getConstraintsForProperty(beanPropertyType.getLeafId());
            if (desc != null) {
                Iterator<ConstraintDescriptor<?>> it = desc
                        .getConstraintDescriptors().iterator();
                while (it.hasNext()) {
                    final ConstraintDescriptor<?> d = it.next();
                    Annotation a = d.getAnnotation();
                    if (a instanceof NotNull) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Convert the value the way {@link com.vaadin.data.util.MethodProperty} does: if the bean field
         * is assignable from the value, return the value directly. Otherwise, try
         * to find a constructor for bean field type that takes a String and call it
         * with value.toString() .
         *
         * @param value the value to convert
         * @return converted value, assignable to the field of the bean
         * @throws {@link com.vaadin.data.Property.ConversionException} if no suitable conversion found or
         *                the target type constructor from string threw an exception
         */
        private Object convertValue(Object value)
                throws Property.ConversionException {
            // Try to assign the compatible value directly
            if (value == null
                    || beanPropertyType.getType().isAssignableFrom(value.getClass())) {
                return value;
            } else {
                try {
                    // Gets the string constructor
                    final Constructor constr = beanPropertyType.getType().getConstructor(
                            new Class[]{String.class});

                    return constr.newInstance(new Object[]{value.toString()});

                } catch (final java.lang.Exception e) {
                    throw new Property.ConversionException(e);
                }
            }
        }

        private String getErrorMessage(final Object value, Class<? extends Annotation>... an) {
            BeanDescriptor beanDesc = validation.getConstraintsForClass(beanType);
            PropertyDescriptor desc = beanDesc
                    .getConstraintsForProperty(propertyId);
            if (desc == null) {
                // validate() reports a conversion error in this case
                return null;
            }
            Iterator<ConstraintDescriptor<?>> it = desc.getConstraintDescriptors()
                    .iterator();
            List<String> exceptions = new ArrayList<String>();
            while (it.hasNext()) {
                final ConstraintDescriptor<?> d = it.next();
                Annotation a = d.getAnnotation();
                boolean skip = false;
                if (an != null && an.length > 0) {
                    skip = true;
                    for (Class<? extends Annotation> t : an) {
                        if (t == a.annotationType()) {
                            skip = false;
                            break;
                        }
                    }
                }
                if (!skip) {
                    String messageTemplate = null;
                    try {
                        Method m = a.getClass().getMethod("message");
                        messageTemplate = (String) m.invoke(a);
                    } catch (Exception ex) {
                        throw new Validator.InvalidValueException(
                                "Annotation must have message attribute");
                    }
                    String msg = validation.getFactory().getMessageInterpolator().interpolate(
                            messageTemplate, new MessageInterpolator.Context() {

                                public Object getValidatedValue() {
                                    return value;
                                }

                                public ConstraintDescriptor<?> getConstraintDescriptor() {
                                    return d;
                                }
                            }, locale);
                    exceptions.add(msg);
                }
            }
            if (exceptions.size() > 0) {
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < exceptions.size(); i++) {
                    if (i != 0) {
                        b.append("<br/>");
                    }
                    b.append(exceptions.get(i));
                }
                return b.toString();
            }
            return null;
        }

    }
}
