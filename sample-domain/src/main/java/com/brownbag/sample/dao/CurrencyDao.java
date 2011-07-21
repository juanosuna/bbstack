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

package com.brownbag.sample.dao;

import com.brownbag.core.dao.EntityDao;
import com.brownbag.sample.entity.Currency;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

import static com.brownbag.sample.dao.CacheSettings.setReadOnly;

@Repository
public class CurrencyDao extends EntityDao<Currency, String> {

    @Override
    public List<Currency> findAll() {
        Query query = getEntityManager().createQuery("SELECT c FROM Currency c ORDER BY c.name");
        setReadOnly(query);

        return query.getResultList();
    }
}
