package com.brownbag.core.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User: Juan
 * Date: 6/9/11
 * Time: 12:14 PM
 */
public class NestedBean {

    @NotNull
    @Size(min = 1, max = 1)
    private String property;

    private String optionalProperty;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getOptionalProperty() {
        return optionalProperty;
    }

    public void setOptionalProperty(String optionalProperty) {
        this.optionalProperty = optionalProperty;
    }
}
