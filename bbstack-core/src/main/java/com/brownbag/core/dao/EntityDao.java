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

package com.brownbag.core.dao;

import com.brownbag.core.entity.IdentifiableEntity;
import com.brownbag.core.util.ReflectionUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class EntityDao<T, ID extends Serializable> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<T> persistentClass;

    public EntityDao() {
        persistentClass = ReflectionUtil.getGenericArgumentType(getClass());
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected Class<T> getPersistentClass() {
        if (persistentClass == null) {
            throw new UnsupportedOperationException();
        }

        return persistentClass;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void remove(T entity) {
        T attachedEntity = getReference(entity);
        getEntityManager().remove(attachedEntity);
    }

    public T getReference(T entity) {
        Object primaryKey = ((IdentifiableEntity) entity).getId();
        return getEntityManager().getReference(getPersistentClass(), primaryKey);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return executeQuery("select c from " + getPersistentClass().getSimpleName() + " c");
    }

    @Transactional(readOnly = true)
    public List<T> findAll(Class<T> t) {
        return executeQuery("select c from " + t.getSimpleName() + " c");
    }

    protected Query createQuery(String query) {
        return getEntityManager().createQuery(query);
    }

    protected org.hibernate.Query createHibernateQuery(String query) {
        Session session = (Session) getEntityManager().getDelegate();
        return session.createQuery(query);
    }

    @Transactional(readOnly = true)
    public List<T> executeQuery(String query) {
        return getEntityManager().createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<T> executeNativeQuery(String query) {
        return getEntityManager().createNativeQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public List<T> executeQuery(String query, int firstResult, int maxResults) {
        Query q = getEntityManager().createQuery(query);
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);

        return q.getResultList();
    }

    @Transactional(readOnly = true)
    public T find(ID id) {
        return getEntityManager().find(getPersistentClass(), id);
    }

    @Transactional(readOnly = true)
    public T find(Class<T> t, ID id) {
        return getEntityManager().find(t, id);
    }

    @Transactional(readOnly = true)
    public T findByBusinessKey(String propertyName, Object propertyValue) {
        Session session = (Session) getEntityManager().getDelegate();

        Criteria criteria = session.createCriteria(getPersistentClass());
        criteria.add(Restrictions.naturalId().set(propertyName, propertyValue));
        criteria.setCacheable(true);

        return (T) criteria.uniqueResult();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(T entity) {
        Query query = getEntityManager().createQuery("delete from " + getPersistentClass().getSimpleName()
                + " c where c = :entity");

        query.setParameter("entity", entity);

        query.executeUpdate();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void persist(Collection<T> entities) {
        for (T entity : entities) {
            persist(entity);
        }
    }

    public void refresh(T entity) {
        getEntityManager().refresh(entity);
    }
}
