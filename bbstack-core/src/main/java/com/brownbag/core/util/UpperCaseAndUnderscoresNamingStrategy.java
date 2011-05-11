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

package com.brownbag.core.util;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class UpperCaseAndUnderscoresNamingStrategy extends ImprovedNamingStrategy {

    public static final String TABLE_PREFIX = "";

    public UpperCaseAndUnderscoresNamingStrategy() {
        super();
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return super.propertyToColumnName(propertyName).toUpperCase();
    }

    @Override
    public String collectionTableName(String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable, String propertyName) {
        return super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName).toUpperCase();
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName).toUpperCase();
    }

    @Override
    public String logicalColumnName(String columnName, String propertyName) {
        return super.logicalColumnName(columnName, propertyName).toUpperCase();
    }

    @Override
    public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName) {
        return TABLE_PREFIX + super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName).toUpperCase();
    }

    @Override
    public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
        return super.logicalCollectionColumnName(columnName, propertyName, referencedColumn).toUpperCase();
    }

    @Override
    public String classToTableName(String className) {
        return TABLE_PREFIX + super.classToTableName(className).toUpperCase();
    }

    @Override
    public String tableName(String tableName) {
        return TABLE_PREFIX + super.tableName(tableName).toUpperCase();
    }

    @Override
    public String columnName(String columnName) {
        return super.columnName(columnName).toUpperCase();
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(joinedColumn, joinedTable).toUpperCase();
    }
}
