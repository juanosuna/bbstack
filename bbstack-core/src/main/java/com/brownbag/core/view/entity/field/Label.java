package com.brownbag.core.view.entity.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: Juan
 * Date: 5/12/11
 * Time: 10:44 PM
 */
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface Label {
    String value() default "";
}
