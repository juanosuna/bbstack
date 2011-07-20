package com.brownbag.sample.entity;

/**
 * User: Juan
 * Date: 6/8/11
 * Time: 11:23 AM
 */
public enum PhoneType {
    HOME("Home"),
    MOBILE("Mobile"),
    BUSINESS("Business");

    private String name;

    PhoneType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
