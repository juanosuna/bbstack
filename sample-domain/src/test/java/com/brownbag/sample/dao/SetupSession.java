package com.brownbag.sample.dao;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.SessionScope;

/**
 * User: Juan
 * Date: 7/14/11
 * Time: 10:14 AM
 */
public class SetupSession extends SessionScope implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
