package com.vaadin.data.util;

/**
 * User: Juan
 * Date: 5/27/11
 * Time: 10:09 PM
 */

import java.util.Map;

@SuppressWarnings("serial")
public class NullCapableBeanItem<BT> extends BeanItem<BT> {

    public NullCapableBeanItem(BT bean,
                               Map<String, VaadinPropertyDescriptor<BT>> propertyDescriptors) {

        super(bean, propertyDescriptors);
    }
}
