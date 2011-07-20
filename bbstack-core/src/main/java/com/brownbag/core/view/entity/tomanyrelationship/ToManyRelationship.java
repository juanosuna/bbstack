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

package com.brownbag.core.view.entity.tomanyrelationship;

import com.brownbag.core.view.entity.EntityComponent;

import javax.annotation.PostConstruct;

/**
 * User: Juan
 * Date: 5/7/11
 * Time: 5:27 PM
 */
public abstract class ToManyRelationship<T> extends EntityComponent<T> {

    protected ToManyRelationship() {
        super();
    }

    public abstract ToManyRelationshipResults getResultsComponent();

    @PostConstruct
    @Override
    public void postConstruct() {
        super.postConstruct();

        addComponent(getResultsComponent());
    }
}