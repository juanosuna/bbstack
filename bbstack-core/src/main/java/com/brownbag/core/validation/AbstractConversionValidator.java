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

package com.brownbag.core.validation;


import com.brownbag.core.view.entity.EntityForm;
import com.brownbag.core.view.entity.field.FormField;
import com.vaadin.data.Validator;

public abstract class AbstractConversionValidator implements Validator {
    private String errorMessage;
    private FormField formField;

    public AbstractConversionValidator(FormField formField, String errorMessage) {
        this.formField = formField;
        this.errorMessage = errorMessage;
    }

    protected AbstractConversionValidator(FormField formField) {
        this.formField = formField;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public FormField getFormField() {
        return formField;
    }

    public abstract void validateImpl(Object value) throws Exception;

    public void validate(Object value) throws InvalidValueException {
        try {
            validateImpl(value);
        } catch (Exception e) {
            formField.setHasConversionError(true);
            EntityForm entityForm = (EntityForm) formField.getFormFields().getForm();
            entityForm.syncTabAndSaveButtonErrors();

            if (getErrorMessage() != null) {
                throw new InvalidValueException(getErrorMessage());
            } else {
                throw new InvalidValueException(e.getMessage());
            }
        }
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

}
