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

import static com.brownbag.core.entity.WritableEntity.SCHEMA;

@Entity
@Table(schema = SCHEMA)
public class Contact extends WritableEntity {

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    private String firstName;

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    private String lastName;

    @Size(min = 1, max = 16)
    private String title;

    @Index(name = "IDX_CONTACT_ACCOUNT")
    @ForeignKey(name = "FK_CONTACT_ACCOUNT")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Account account;

    @Past
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Pattern(regexp = "^\\d+$", message = "Must contain only 9 digits")
    @Size(min = 9, max = 9)
    private String socialSecurityNumber;

    @Valid
    @Index(name = "IDX_CONTACT_ADDRESS")
    @ForeignKey(name = "FK_CONTACT_ADDRESS")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Address address = new Address();

    public Contact() {
    }

    public Contact(String firstName, String lastName, String socialSecurityNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return getLastName() + ", " + getFirstName();
    }

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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @PreRemove
	public void preRemove() {
		setAccount(null);
	}
}