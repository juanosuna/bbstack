package com.brownbag.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Juan
 * Date: 6/4/11
 * Time: 11:25 PM
 */
public class MethodDelegate {
    private Object target;
    private Method method;

    public MethodDelegate(Object target, String methodName, Class<?>... parameterTypes) {
        this.target = target;
        try {
            method = target.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object execute(Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
