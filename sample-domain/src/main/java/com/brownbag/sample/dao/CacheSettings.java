package com.brownbag.sample.dao;

import javax.persistence.Query;

/**
 * User: Juan
 * Date: 7/21/11
 */
public class CacheSettings {

    public static void setReadOnly(Query query) {
        query.setHint("org.hibernate.cacheable", true);
        query.setHint("org.hibernate.cacheRegion", "ReadOnlyQuery");
        query.setHint("org.hibernate.readOnly", true);
    }
}
