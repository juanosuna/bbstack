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

package com.brownbag.sample.entity;


import com.brownbag.core.entity.WritableEntity;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table
public class Contact extends WritableEntity {

    private String firstName;

    private String lastName;

    private String title;

    @Index(name = "IDX_CONTACT_ACCOUNT")
    @ForeignKey(name = "FK_CONTACT_ACCOUNT")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Account account;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String socialSecurityNumber;

    private boolean doNotCall;

    @Index(name = "IDX_CONTACT_PRIMARY_ADDRESS")
    @ForeignKey(name = "FK_CONTACT_PRIMARY_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address = new Address(AddressType.PRIMARY);

    @Index(name = "IDX_CONTACT_OTHER_ADDRESS")
    @ForeignKey(name = "FK_CONTACT_OTHER_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address otherAddress;

    @Lob
    private String note;

    public Contact() {
    }

    public Contact(String firstName, String lastName, String socialSecurityNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return getLastName() + ", " + getFirstName();
    }

    @Size(min = 1, max = 16)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Past
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Pattern(regexp = "^\\d+$", message = "Must contain only 9 digits")
    @Size(min = 9, max = 9)
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public boolean isDoNotCall() {
        return doNotCall;
    }

    public void setDoNotCall(boolean doNotCall) {
        this.doNotCall = doNotCall;
    }

    @Valid
    @NotNull
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        if (address != null) {
            address.setType(AddressType.PRIMARY);
        }
        this.address = address;
    }

    @Valid
    public Address getOtherAddress() {
        return otherAddress;
    }

    public void setOtherAddress(Address otherAddress) {
        if (otherAddress != null) {
            otherAddress.setType(AddressType.OTHER);
        }
        this.otherAddress = otherAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @PreRemove
    public void preRemove() {
        setAccount(null);
    }

}