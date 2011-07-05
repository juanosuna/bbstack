package com.vaadin.data.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: Juan
 * Date: 5/28/11
 * Time: 12:00 AM
 */
public class NullCapableNestedMethodProperty extends NestedMethodProperty {
    private Object instance;

    public NullCapableNestedMethodProperty(Object instance, String propertyName) {
        super(instance, propertyName);
        this.instance = instance;
    }

    public NullCapableNestedMethodProperty(Class<?> instanceClass, String propertyName) {
        super(instanceClass, propertyName);
    }

    @Override
    public Object getValue() {
        if (hasNullInPropertyPath()) {
            return null;
        } else {
            return super.getValue();
        }
    }

    public boolean hasNullInPropertyPath() {
        try {
            List<Method> getMethods = getGetMethods();
            Object object = instance;
            for (Method getMethod : getMethods) {
                if (object == null) {
                    return true;
                } else {
                    object = getMethod.invoke(object);
                }
            }
            return false;
        } catch (final InvocationTargetException e) {
            throw new MethodProperty.MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }

    @Override
    protected void invokeSetMethod(Object value) {
        if (hasNullInPropertyPath()) {
            if (value != null) {
                fillNullsInPropertyPath();
                super.invokeSetMethod(value);
            }
        } else {
            super.invokeSetMethod(value);
        }
    }

    private void fillNullsInPropertyPath() {
        try {
            List<Method> getMethods = getGetMethods();
            Object parent = null;
            Method parentGetMethod = null;
            Object object = instance;
            for (Method getMethod : getMethods) {
                if (object == null && parent != null) {
                    Class returnType = parentGetMethod.getReturnType();
                    String getMethodName = parentGetMethod.getName();
                    Class declaringClass = parentGetMethod.getDeclaringClass();
                    Method setMethod = getSetMethod(declaringClass, returnType, getMethodName);
                    Object fillerInstance = returnType.newInstance();
                    setMethod.invoke(parent, fillerInstance);
                } else {
                    parent = object;
                    parentGetMethod = getMethod;
                    object = getMethod.invoke(parent);
                }
            }
        } catch (final InvocationTargetException e) {
            throw new MethodProperty.MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }

    private Method getSetMethod(Class containerType, Class propertyType, String getMethodName) {
        try {
            String setMethodName = "set" + getMethodName.substring(3);
            return containerType.getMethod(setMethodName, new Class[]{propertyType});
        } catch (NoSuchMethodException e) {
            throw new MethodProperty.MethodException(this, e);
        }
    }
}
