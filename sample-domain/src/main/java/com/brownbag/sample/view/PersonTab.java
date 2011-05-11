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

package com.brownbag.sample.view;

import com.brownbag.core.ui.EntityTab;
import com.brownbag.sample.entity.Person;
import org.springframework.context.annotation.Scope;

@org.springframework.stereotype.Component
@Scope("session")
public class PersonTab extends EntityTab<Person> {
    @Override
    public String displayName() {
        return "People";
    }
}

