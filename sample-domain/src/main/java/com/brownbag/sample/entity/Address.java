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
import com.brownbag.core.validation.PatternIfThen;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@PatternIfThen(ifProperty = "country.id", ifRegex = "^US$",
        thenProperty = "zipCode", thenRegex = "^\\d{5}$|^\\d{5}$",
        message = "US zip code must be 5 or 9 digits")
@Entity
@Table
public class Address extends WritableEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    private AddressType type = AddressType.PHYSICAL;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 16)
    private String street;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 16)
    private String city;

    private String zipCode;

    @Index(name = "IDX_ADDRESS_STATE")
    @ForeignKey(name = "FK_ADDRESS_STATE")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private State state;

    @Index(name = "IDX_ADDRESS_COUNTRY")
    @ForeignKey(name = "FK_ADDRESS_COUNTRY")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Country country;

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<Account>();

    public Address() {
    }

    public Address(AddressType type) {
        this.type = type;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }
}