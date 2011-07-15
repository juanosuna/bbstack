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

package com.brownbag.sample.view.account.related;

import com.brownbag.core.dao.ToManyRelationshipQuery;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.core.view.entity.tomanyrelationship.ToManyRelationship;
import com.brownbag.core.view.entity.tomanyrelationship.ToManyRelationshipResults;
import com.brownbag.sample.dao.OpportunityDao;
import com.brownbag.sample.entity.Account;
import com.brownbag.sample.entity.Opportunity;
import com.brownbag.sample.view.select.OpportunitySelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class RelatedOpportunities extends ToManyRelationship<Opportunity> {

    @Resource
    private OpportunityDao opportunityDao;

    @Resource
    private RelatedOpportunitiesQuery relatedOpportunitiesQuery;

    @Resource
    private RelatedOpportunitiesResults relatedOpportunitiesResults;

    @Override
    public String getEntityCaption() {
        return "Company Sales Opportunities";
    }

    @Override
    public OpportunityDao getEntityDao() {
        return opportunityDao;
    }

    @Override
    public ToManyRelationshipQuery getEntityQuery() {
        return relatedOpportunitiesQuery;
    }

    @Override
    public RelatedOpportunitiesResults getResultsComponent() {
        return relatedOpportunitiesResults;
    }

    @Component
    @Scope("prototype")
    public static class RelatedOpportunitiesQuery extends ToManyRelationshipQuery<Opportunity, Account> {

        @Autowired
        private OpportunityDao opportunityDao;

        private Account account;

        @Override
        public void setParent(Account account) {
            this.account = account;
        }

        @Override
        public Account getParent() {
            return account;
        }

        @Override
        public List<Opportunity> execute() {
            return opportunityDao.execute(this);
        }

        @Override
        public List<Predicate> buildCriteria(CriteriaBuilder builder, Root<Opportunity> rootEntity) {
            List<Predicate> criteria = new ArrayList<Predicate>();

            if (!isEmpty(account)) {
                ParameterExpression<Account> p = builder.parameter(Account.class, "account");
                criteria.add(builder.equal(rootEntity.get("account"), p));
            }

            return criteria;
        }

        @Override
        public void setParameters(TypedQuery typedQuery) {
            if (!isEmpty(account)) {
                typedQuery.setParameter("account", account);
            }
        }

        @Override
        public Path buildOrderByPath(Root<Opportunity> rootEntity) {
            if (getOrderByPropertyId().equals("account.name")) {
                return rootEntity.get("account").get("name");

            } else {
                return null;
            }
        }

        @Override
        public void addFetchJoins(Root<Opportunity> rootEntity) {
            rootEntity.fetch("account", JoinType.LEFT);
        }

        @Override
        public String toString() {
            return "RelatedOpportunities{" +
                    "account='" + account + '\'' +
                    '}';
        }
    }

    @Component
    @Scope("prototype")
    public static class RelatedOpportunitiesResults extends ToManyRelationshipResults<Opportunity> {

        @Resource
        private OpportunitySelect opportunitySelect;

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "account.name",
                    "salesStage",
                    "expectedCloseDate",
                    "amount",
                    "lastModified",
                    "modifiedBy"
            });

            displayFields.getField("name").setLabel("Name");
            displayFields.getField("account.name").setLabel("Account");
        }

        @Override
        public OpportunitySelect getEntitySelect() {
            return opportunitySelect;
        }

        @Override
        public String getPropertyId() {
            return "account";
        }

        @Override
        public String getEntityCaption() {
            return "Opportunities";
        }
    }
}

