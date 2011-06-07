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
import com.brownbag.core.view.entity.EntityQuery;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.entity.Country;
import com.brownbag.sample.entity.State;
import com.brownbag.sample.view.account.contactmanyselect.ContactQueryManySelect;
import com.brownbag.sample.view.contact.ContactQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ContactDao extends EntityDao<Contact, Long> {

    public List<Contact> find(ContactQueryManySelect query) {
        List<Long> count = findImpl(query, true);
        query.setResultCount(count.get(0));

        if (query.getResultCount() > 0) {
            List<Long> ids = findImpl(query, false);
            return findByIds(ids, query.getOrderByPropertyId(), query.getOrderDirection());
        } else {
            return new ArrayList<Contact>();
        }
    }

    private List<Long> findImpl(ContactQueryManySelect contactQuery, boolean isCount) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> c = b.createQuery(Long.class);
        Root<Contact> contact = c.from(Contact.class);

        if (isCount) {
            if (contactQuery.getParent() == null) {
                List<Long> results = new ArrayList<Long>();
                results.add(0L);
                return results;
            } else {
                c.select(b.count(contact));
            }
        } else {
            c.select(contact.<Long>get("id"));
        }

        List<Predicate> criteria = new ArrayList<Predicate>();
        if (contactQuery.getParent() != null) {
            ParameterExpression<Account> p = b.parameter(Account.class, "account");
            criteria.add(b.equal(contact.get("account"), p));
        }

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (!isCount && contactQuery.getOrderByPropertyId() != null) {
            Path path;
            String orderField = contactQuery.getOrderByPropertyId();
            if (orderField.equals("address.country")) {
                path = contact.get("address").get("country");

            } else if (orderField.equals("address.state")) {
                path = contact.get("address").get("state");
            } else {
                path = contact.get(orderField);
            }
            if (contactQuery.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Long> q = getEntityManager().createQuery(c);
        if (contactQuery.getParent() != null) {
            q.setParameter("account", contactQuery.getParent());
        }

        if (!isCount) {
            q.setFirstResult(contactQuery.getFirstResult());
            q.setMaxResults(contactQuery.getPageSize());
        }

        return q.getResultList();
    }

    public List<Contact> find(ContactQuery query) {
        List<Long> count = findImpl(query, true);
        query.setResultCount(count.get(0));

        List<Long> ids = findImpl(query, false);
        if (ids.size() > 0) {
            return findByIds(ids, query.getOrderByPropertyId(), query.getOrderDirection());
        } else {
            return new ArrayList<Contact>();
        }
    }

    private List<Long> findImpl(ContactQuery query, boolean isCount) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> c = b.createQuery(Long.class);
        Root<Contact> contact = c.from(Contact.class);

        if (isCount) {
            c.select(b.count(contact));
        } else {
            c.select(contact.<Long>get("id"));
        }

        List<Predicate> criteria = new ArrayList<Predicate>();
        if (query.getLastName() != null) {
            ParameterExpression<String> p = b.parameter(String.class, "lastName");
            criteria.add(b.like(b.upper(contact.<String>get("lastName")), p));
        }
        if (query.getState() != null) {
            ParameterExpression<State> p = b.parameter(State.class, "state");
            criteria.add(b.equal(contact.get("address").get("state"), p));
        }
        if (query.getCountry() != null) {
            ParameterExpression<Country> p = b.parameter(Country.class, "country");
            criteria.add(b.equal(contact.get("address").get("country"), p));
        }

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (!isCount && query.getOrderByPropertyId() != null) {
            Path path;
            String orderField = query.getOrderByPropertyId();
            if (orderField.equals("address.country")) {
                path = contact.get("address").get("country");
            } else if (orderField.equals("address.state")) {
                path = contact.get("address").get("state");
            } else if (orderField.equals("account.name")) {
                path = contact.get("account").get("name");
            } else {
                path = contact.get(orderField);
            }
            if (query.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Long> q = getEntityManager().createQuery(c);
        if (query.getLastName() != null) {
            q.setParameter("lastName", "%" + query.getLastName().toUpperCase() + "%");
        }
        if (query.getState() != null) {
            q.setParameter("state", query.getState());
        }
        if (query.getCountry() != null) {
            q.setParameter("country", query.getCountry());
        }

        if (!isCount) {
            q.setFirstResult(query.getFirstResult());
            q.setMaxResults(query.getPageSize());
        }

        return q.getResultList();
    }

    public List<Contact> findByIds(List<Long> ids, String orderField, EntityQuery.OrderDirection orderDirection) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Contact> c = b.createQuery(Contact.class);
        Root<Contact> contact = c.from(Contact.class);
        c.select(contact);

        contact.fetch("address", JoinType.LEFT);
        contact.fetch("account", JoinType.LEFT);

        List<Predicate> criteria = new ArrayList<Predicate>();
        ParameterExpression<List> p = b.parameter(List.class, "ids");
        criteria.add(b.in(contact.get("id")).value(p));

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (orderField != null) {
            Path path;
            if (orderField.equals("address.country")) {
                path = contact.get("address").get("country");
            } else if (orderField.equals("address.state")) {
                path = contact.get("address").get("state");
            } else if (orderField.equals("account.name")) {
                path = contact.get("account").get("name");
            } else {
                path = contact.get(orderField);
            }
            if (orderDirection.equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Contact> q = getEntityManager().createQuery(c);
        q.setParameter("ids", ids);

        return q.getResultList();
    }
}
