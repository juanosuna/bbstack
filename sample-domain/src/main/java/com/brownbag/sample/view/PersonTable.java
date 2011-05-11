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

import com.brownbag.core.ui.EntityTable;
import com.brownbag.sample.entity.Person;
import org.springframework.context.annotation.Scope;

import java.util.LinkedHashMap;

/**
 * User: Juan
 * Date: 2/10/11
 * Time: 12:50 AM
 */
@org.springframework.stereotype.Component
@Scope("session")
public class PersonTable extends EntityTable<Person> {

    public static final LinkedHashMap<String, String> FIELDS = new LinkedHashMap<String, String>() {
        {
            put("id", "Id");
            put("fullName", "Name");
            put("address.state", "State");
            put("address.country", "Country");
            put("lastModified", "Last Modified");
            put("lastModifiedBy", "Modified By");
        }
    };

    @Override
    public LinkedHashMap<String, String> getFields() {
        return FIELDS;
    }
}
