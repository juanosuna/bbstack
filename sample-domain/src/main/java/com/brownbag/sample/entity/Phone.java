package com.brownbag.sample.entity;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User: Juan
 * Date: 7/19/11
 */
@Embeddable
public class Phone implements Serializable {
    private Integer countryCode;
    private Long number;

    @Enumerated(EnumType.STRING)
    private PhoneType type = PhoneType.BUSINESS;

    public Phone() {
    }

    public Phone(String fullNumber, String regionCode) throws NumberParseException {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(fullNumber, regionCode);

        this.countryCode = phoneNumber.getCountryCode();
        this.number = phoneNumber.getNationalNumber();
    }

    @Transient
    public String getFormatted(String defaultRegionCode) {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(countryCode);
        phoneNumber.setNationalNumber(number);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumberUtil.PhoneNumberFormat format;
        if (phoneUtil.getRegionCodeForNumber(phoneNumber).equals(defaultRegionCode)) {
            format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
        } else {
            format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
        }

        return phoneUtil.format(phoneNumber, format);
    }

    @NotNull
    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    @NotNull
    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    @NotNull
    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }
}
