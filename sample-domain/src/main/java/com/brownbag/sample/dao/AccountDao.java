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
import com.brownbag.sample.view.account.AccountQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class AccountDao extends EntityDao<Account, Long> {

    public List<Account> find(AccountQuery accountQuery) {
        List<Long> count = findImpl(accountQuery, true);
        accountQuery.setResultCount(count.get(0));

        List<Long> ids = findImpl(accountQuery, false);
        if (ids.size() > 0) {
            return findByIds(ids, accountQuery.getOrderByField(), accountQuery.getOrderDirection());
        } else {
            return new ArrayList<Account>();
        }
    }

    private List<Long> findImpl(AccountQuery accountQuery, boolean isCount) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> c = b.createQuery(Long.class);
        Root<Account> account = c.from(Account.class);

        if (isCount) {
            c.select(b.count(account));
        } else {
            c.select(account.<Long>get("id"));
        }

        List<Predicate> criteria = new ArrayList<Predicate>();
        if (accountQuery.getName() != null) {
            ParameterExpression<String> p = b.parameter(String.class, "name");
            criteria.add(b.like(b.upper(account.<String>get("name")), p));
        }
        if (accountQuery.getState() != null) {
            ParameterExpression<State> p = b.parameter(State.class, "state");
            criteria.add(b.equal(account.get("address").get("state"), p));
        }
        if (accountQuery.getCountry() != null) {
            ParameterExpression<Country> p = b.parameter(Country.class, "country");
            criteria.add(b.equal(account.get("address").get("country"), p));
        }

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (!isCount && accountQuery.getOrderByField() != null) {
            Path path;
            String orderField = accountQuery.getOrderByField();
            if (orderField.equals("address.country")) {
                path = account.get("address").get("country");

            } else if (orderField.equals("address.state")) {
                path = account.get("address").get("state");
            } else {
                path = account.get(orderField);
            }
            if (accountQuery.getOrderDirection().equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Long> q = getEntityManager().createQuery(c);
        if (accountQuery.getName() != null) {
            q.setParameter("name", "%" + accountQuery.getName().toUpperCase() + "%");
        }
        if (accountQuery.getState() != null) {
            q.setParameter("state", accountQuery.getState());
        }
        if (accountQuery.getCountry() != null) {
            q.setParameter("country", accountQuery.getCountry());
        }

        if (!isCount) {
            q.setFirstResult(accountQuery.getFirstResult());
            q.setMaxResults(accountQuery.getPageSize());
        }

        return q.getResultList();
    }

    public List<Account> findByIds(List<Long> ids, String orderField, EntityQuery.OrderDirection orderDirection) {
        CriteriaBuilder b = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Account> c = b.createQuery(Account.class);
        Root<Account> account = c.from(Account.class);
        c.select(account);

        account.fetch("address");

        List<Predicate> criteria = new ArrayList<Predicate>();
        ParameterExpression<List> p = b.parameter(List.class, "ids");
        criteria.add(b.in(account.get("id")).value(p));

        c.where(b.and(criteria.toArray(new Predicate[0])));

        if (orderField != null) {
            Path path;
            if (orderField.equals("address.country")) {
                path = account.get("address").get("country");

            } else if (orderField.equals("address.state")) {
                path = account.get("address").get("state");
            } else {
                path = account.get(orderField);
            }
            if (orderDirection.equals(EntityQuery.OrderDirection.ASC)) {
                c.orderBy(b.asc(path));
            } else {
                c.orderBy(b.desc(path));
            }
        }

        TypedQuery<Account> q = getEntityManager().createQuery(c);
        q.setParameter("ids", ids);

        return q.getResultList();
    }

    @Override
    public void remove(Account entity) {
        super.remove(entity);
    }
}
