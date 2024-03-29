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
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.sample.service;

import org.jboss.resteasy.client.spring.RestClientProxyFactoryBean;

import java.net.URI;

public class RestClientService {

    public <T> T create(String uri, Class<T> clazz) throws Exception {
        RestClientProxyFactoryBean restClientFactory = new RestClientProxyFactoryBean();
        restClientFactory.setBaseUri(new URI(uri));
        restClientFactory.setServiceInterface(clazz);
        restClientFactory.afterPropertiesSet();
        return (T) restClientFactory.getObject();
    }
}
