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
import com.brownbag.core.query.EntityQuery;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.Person;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.query.PersonQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class PersonDao extends EntityDao<Person, Long> {

    public List<Person> find(PersonQuery personQuery) {
        List<Long> count = findImpl(personQuery, true);
        personQuery.setResultCount(count.get(0));

        List<Long> ids = findImpl(personQuery, false);
        if (ids.size() > 0) {
            return findByIds(ids, personQuery.getOrderByField(), personQuery.getOrderDirection());
        } else {
            return new ArrayList<Person>();
        }
    }

    private List<Long> findImpl(PersonQuery personQuery, boolean isCount) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> c = b.createQuery(Long.class);
        Root<Person> person = c.from(Person.class);

        if (isCount) {
            c.select(b.count(person));
        } else {
            c.select(person.<Long>get("id"));
        }

        List<Predicate> criteria = new ArrayList<Predicate>();
        if (personQuery.getLastName() != null) {
            ParameterExpression<String> p = b.parameter(String.class, "lastName");
            criteria.add(b.like(person.<String>get("lastName"), p));
        }
        if (personQuery.getState() != null) {
            ParameterExpression<State> p = b.parameter(State.class, "state");
            criteria.add(b.equal(person.get("address").get("state"), p));
        }
        if (personQuery.getCountry() != null) {
            ParameterExpression<Country> p = b.parameter(Country.class, "country");
            criteria.add(b.equal(person.get("address").get("country"), p));
        }

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (!isCount && personQuery.getOrderByField() != null) {
            Path path;
            String orderField = personQuery.getOrderByField();
            if (orderField.equals("address.country")) {
                path = person.get("address").get("country");

            } else if (orderField.equals("address.state")) {
                path = person.get("address").get("state");
            } else {
                path = person.get(orderField);
            }
            if (personQuery.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Long> q = getEntityManager().createQuery(c);
        if (personQuery.getLastName() != null) {
            q.setParameter("lastName", "%" + personQuery.getLastName() + "%");
        }
        if (personQuery.getState() != null) {
            q.setParameter("state", personQuery.getState());
        }
        if (personQuery.getCountry() != null) {
            q.setParameter("country", personQuery.getCountry());
        }

        if (!isCount) {
            q.setFirstResult(personQuery.getFirstResult());
            q.setMaxResults(personQuery.getPageSize());
        }

        return q.getResultList();
    }

    public List<Person> findByIds(List<Long> ids, String orderField, EntityQuery.OrderDirection orderDirection) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Person> c = b.createQuery(Person.class);
        Root<Person> person = c.from(Person.class);
        c.select(person);

        person.fetch("address");

        List<Predicate> criteria = new ArrayList<Predicate>();
        ParameterExpression<List> p = b.parameter(List.class, "ids");
        criteria.add(b.in(person.get("id")).value(p));

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (orderField != null) {
            Path path;
            if (orderField.equals("address.country")) {
                path = person.get("address").get("country");

            } else if (orderField.equals("address.state")) {
                path = person.get("address").get("state");
            } else {
                path = person.get(orderField);
            }
            if (orderDirection.equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Person> q = getEntityManager().createQuery(c);
        q.setParameter("ids", ids);

        return q.getResultList();
    }
}
