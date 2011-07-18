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
import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Account extends WritableEntity {

    private String name;

    private Integer numberOfEmployees;

    private BigDecimal annualRevenue;

    @Index(name = "IDX_ACCOUNT_CURRENCY")
    @ForeignKey(name = "FK_ACCOUNT_CURRENCY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    @Index(name = "IDX_ACCOUNT_TYPE")
    @ForeignKey(name = "FK_ACCOUNT_TYPE")
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AccountType> types = new HashSet<AccountType>();

    @Index(name = "IDX_ACCOUNT_INDUSTRY")
    @ForeignKey(name = "FK_ACCOUNT_INDUSTRY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Industry industry;

    @Index(name = "IDX_ACCOUNT_ADDRESS")
    @ForeignKey(name = "FK_ACCOUNT_ADDRESS")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address = new Address();

    @OneToMany(mappedBy = "account")
    private Set<Contact> contacts = new HashSet<Contact>();

    @OneToMany(mappedBy = "account")
    private Set<Opportunity> opportunities = new HashSet<Opportunity>();

    public Account() {
    }

    @NotBlank
    @NotNull
    @Size(min = 1, max = 16)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Min(0)
    public Integer getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(Integer numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    @Min(0)
    public BigDecimal getAnnualRevenue() {
        return annualRevenue;
    }

    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
    }

    public void setAnnualRevenue(double annualRevenue) {
        setAnnualRevenue(new BigDecimal(annualRevenue));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getAnnualRevenueFormattedInCurrency() {
        if (getCurrency() == null) {
            return null;
        } else {
            Format format = new DecimalFormat("###,###");
            if (getCurrency() == null) {
                return format.format(getAnnualRevenue());
            } else {
                return format.format(getAnnualRevenue()) + " " + getCurrency().getId();
            }
        }
    }

    public Set<AccountType> getTypes() {
        return types;
    }

    public void setTypes(Set<AccountType> type) {
        this.types = type;
    }

    public void addType(AccountType type) {
        getTypes().add(type);
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    @Valid
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

    public Set<Opportunity> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(Set<Opportunity> opportunities) {
        this.opportunities = opportunities;
    }

    @PreRemove
    public void preRemove() {
        for (Contact contact : getContacts()) {
            contact.setAccount(null);
        }
    }
}