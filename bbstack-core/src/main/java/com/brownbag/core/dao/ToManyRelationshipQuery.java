package com.brownbag.core.dao;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * User: Juan
 * Date: 7/14/11
 * Time: 11:23 AM
 */
public abstract class ToManyRelationshipQuery<T, P> extends StructuredEntityQuery<T> {

    public abstract void setParent(P p);

    public abstract P getParent();

    public abstract List<Predicate> buildCriteria(CriteriaBuilder builder, Root<T> rootEntity);

    public abstract void setParameters(TypedQuery typedQuery);

    public abstract Path buildOrderByPath(Root<T> rootEntity);

    public abstract void addFetchJoins(Root<T> rootEntity);

}