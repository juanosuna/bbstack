/*
 * BROWN BAG CONFIDENTIAL
 *
 * Copyright (c) 2011 Brown Bag Consulting LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyrightlaw.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.dao;

import com.brownbag.core.dao.EntityDao;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

import static com.brownbag.sample.dao.CacheSettings.setReadOnly;

@Repository
public class StateDao extends EntityDao<State, String> {
    public List<State> findByCountry(Country country) {
        Query query = getEntityManager().createQuery("SELECT s FROM State s WHERE s.country = :country ORDER BY s.displayName");
        query.setParameter("country", country);
        setReadOnly(query);

        return query.getResultList();
    }

    @Override
    public List<State> findAll() {
        Query query = getEntityManager().createQuery("SELECT s FROM State s ORDER BY s.displayName");
        setReadOnly(query);

        return query.getResultList();
    }
}
