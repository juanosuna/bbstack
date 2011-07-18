package com.brownbag.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * User: Juan
 * Date: 7/18/11
 */
@Target({METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = AssertTrueForPropertyValidator.class)
@Documented
public @interface AssertTrueForProperty {
    String message() default "{javax.validation.constraints.AssertTrue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String property();
}
