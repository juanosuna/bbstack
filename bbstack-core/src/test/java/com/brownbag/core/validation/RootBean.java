package com.brownbag.core.validation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * User: Juan
 * Date: 6/9/11
 * Time: 12:13 PM
 */
public class RootBean {

    @Valid
    private NestedBean nestedBean;

    private NestedBean ignoredNestedBean;

    @Valid
    @NotNull
    private NestedBean notNullNestedBean;

    public NestedBean getNestedBean() {
        return nestedBean;
    }

    public void setNestedBean(NestedBean nestedBean) {
        this.nestedBean = nestedBean;
    }

    public NestedBean getIgnoredNestedBean() {
        return ignoredNestedBean;
    }

    public void setIgnoredNestedBean(NestedBean ignoredNestedBean) {
        this.ignoredNestedBean = ignoredNestedBean;
    }

    public NestedBean getNotNullNestedBean() {
        return notNullNestedBean;
    }

    public void setNotNullNestedBean(NestedBean notNullNestedBean) {
        this.notNullNestedBean = notNullNestedBean;
    }
}
