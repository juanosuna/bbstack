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

package com.brownbag.sample.view.select;

import com.brownbag.core.view.entity.entityselect.EntitySelect;
import com.brownbag.core.view.entity.entityselect.EntitySelectResults;
import com.brownbag.core.view.entity.field.DisplayFields;
import com.brownbag.sample.dao.ContactDao;
import com.brownbag.sample.entity.Contact;
import com.brownbag.sample.view.contact.ContactQuery;
import com.brownbag.sample.view.contact.ContactSearchForm;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class ContactSelect extends EntitySelect<Contact> {

    @Resource
    private ContactSearchForm contactSearchForm;

    @Resource
    private ContactSelectResults contactSelectResults;

    @Override
    public ContactSearchForm getSearchForm() {
        return contactSearchForm;
    }

    @Override
    public ContactSelectResults getResultsComponent() {
        return contactSelectResults;
    }


    @Component
    @Scope("prototype")
    public static class ContactSelectResults extends EntitySelectResults<Contact> {

        @Resource
        private ContactDao contactDao;

        @Resource
        private ContactQuery contactQuery;

        @Override
        public ContactDao getEntityDao() {
            return contactDao;
        }

        @Override
        public ContactQuery getEntityQuery() {
            return contactQuery;
        }

        @Override
        public void configureFields(DisplayFields displayFields) {
            displayFields.setPropertyIds(new String[]{
                    "name",
                    "address.state.code",
                    "address.country",
                    "lastModified",
                    "modifiedBy"
            });

            displayFields.setLabel("address.state.code", "State");
            displayFields.setSortable("name", false);
        }
    }
}

