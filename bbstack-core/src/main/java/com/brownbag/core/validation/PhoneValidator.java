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

package com.brownbag.core.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, Object> {

    private ValidPhone validPhone;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        validPhone = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String fullNumber = (String) value;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            if (fullNumber.matches(".*[a-zA-Z]+.*")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Phone number may not contain letters").addConstraintViolation();
                return false;
            }

            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(fullNumber, validPhone.defaultRegionCode());

            PhoneNumberUtil.ValidationResult result = phoneUtil.isPossibleNumberWithReason(phoneNumber);
            if (result == PhoneNumberUtil.ValidationResult.IS_POSSIBLE) {
                if (phoneUtil.isValidNumber(phoneNumber)) {
                    return true;
                } else {
                    String regionCode = phoneUtil.getRegionCodeForNumber(phoneNumber);
                    if (regionCode == null) {
                        regionCode = validPhone.defaultRegionCode();
                    }
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Invalid phone number for region: " + regionCode
                                    + ". Use format: " + getExampleNumber(regionCode)).addConstraintViolation();
                    return false;
                }
            } else {
                String message = null;
                String regionCode = phoneUtil.getRegionCodeForNumber(phoneNumber);
                if (regionCode == null) {
                    regionCode = validPhone.defaultRegionCode();
                }

                switch (result) {
                    case INVALID_COUNTRY_CODE:
                        message = "Invalid region code: " + regionCode;
                        break;
                    case TOO_SHORT:
                        message = "Phone number invalid for region: " + regionCode
                                + ". Use format: " + getExampleNumber(regionCode);
                        break;
                    case TOO_LONG:
                        message = "Phone number invalid for region: " + regionCode
                                + ". Use format: " + getExampleNumber(regionCode);
                        break;
                }
                if (message != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                }
                return false;
            }
        } catch (NumberParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()
                    + ". Use format: " + getExampleNumber("US")).addConstraintViolation();
            return false;
        }
    }

    private String getExampleNumber(String regionCode) {
        if (regionCode == null) {
            regionCode = validPhone.defaultRegionCode();
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber example = phoneUtil.getExampleNumber(regionCode);
        PhoneNumberUtil.PhoneNumberFormat format;
        if (regionCode.equals(validPhone.defaultRegionCode())) {
            format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
        } else {
            format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
        }

        return phoneUtil.format(example, format);
    }
}
