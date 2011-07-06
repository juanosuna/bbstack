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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Account extends WritableEntity {

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    private String name;

    @Min(0)
    private Integer numberOfEmployees;

    @Min(0)
    private BigDecimal annualRevenue;

    private Currency annualRevenueCurrency;

    @Index(name = "IDX_ACCOUNT_TYPE")
    @ForeignKey(name = "FK_ACCOUNT_TYPE")
    @ManyToOne(fetch = FetchType.LAZY)
    private AccountType type;

    @Index(name = "IDX_ACCOUNT_INDUSTRY")
    @ForeignKey(name = "FK_ACCOUNT_INDUSTRY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Industry industry;

    @Valid
    @Index(name = "IDX_ACCOUNT_ADDRESS")
    @ForeignKey(name = "FK_ACCOUNT_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address = new Address();

    @OneToMany(mappedBy = "account")
    private Set<Contact> contacts = new HashSet<Contact>();

    public Account() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public Currency getAnnualRevenueCurrency() {
        return annualRevenueCurrency;
    }

    public void setAnnualRevenueCurrency(Currency annualRevenueCurrency) {
        this.annualRevenueCurrency = annualRevenueCurrency;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    @PreRemove
    public void preRemove() {
        for (Contact contact : getContacts()) {
            contact.setAccount(null);
        }
    }
}