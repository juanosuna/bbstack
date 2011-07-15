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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table
public class Opportunity extends WritableEntity {

    @NotBlank
    @NotNull
    @Size(min = 1, max = 32)
    private String name;

    @Index(name = "IDX_OPPORTUNITY_ACCOUNT")
    @ForeignKey(name = "FK_OPPORTUNITY_ACCOUNT")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Account account;

    @Index(name = "IDX_OPPORTUNITY_SALES_STAGE")
    @ForeignKey(name = "FK_OPPORTUNITY_SALES_STAGE")
    @ManyToOne(fetch = FetchType.LAZY)
    private SalesStage salesStage;

    @Temporal(TemporalType.DATE)
    private Date expectedCloseDate;

    @Min(0)
    private BigDecimal amount;

    @Index(name = "IDX_OPPORTUNITY_CURRENCY")
    @ForeignKey(name = "FK_OPPORTUNITY_CURRENCY")
    @ManyToOne(fetch = FetchType.LAZY)
    private Currency currency;

    private float probability;

    private float commission;

    @Lob
    private String description;

    public Opportunity() {
    }

    public Opportunity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SalesStage getSalesStage() {
        return salesStage;
    }

    public void setSalesStage(SalesStage salesStage) {
        this.salesStage = salesStage;
    }

    public Date getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(Date expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(double amount) {
        setAmount(new BigDecimal(amount));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public float getCommission() {
        return commission;
    }

    public void setCommission(float commission) {
        this.commission = commission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PreRemove
    public void preRemove() {
        setAccount(null);
    }
}