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


import com.brownbag.core.entity.ReferenceEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

import static com.brownbag.core.entity.WritableEntity.SCHEMA;

@Entity
@Table(schema = SCHEMA)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "ReadOnly")
public class AccountType extends ReferenceEntity {

    public AccountType() {
    }

    public AccountType(String id) {
        super(id);
    }

    public AccountType(String id, String name) {
        super(id, name);
    }
}
