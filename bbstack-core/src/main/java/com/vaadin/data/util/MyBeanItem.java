package com.vaadin.data.util;

/**
 * User: Juan
 * Date: 5/27/11
 * Time: 10:09 PM
 */

import java.util.Map;

@SuppressWarnings("serial")
public class MyBeanItem<BT> extends BeanItem<BT> {

    public MyBeanItem(BT bean,
                      Map<String, VaadinPropertyDescriptor<BT>> propertyDescriptors) {

        super(bean, propertyDescriptors);
    }
}
