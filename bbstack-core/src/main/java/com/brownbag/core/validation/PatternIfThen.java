/*
 * BROWN BAG CONFIDENTIAL
 *
 * Brown Bag Consulting LLC
 * Copyright (c) 2011. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Brown Bag Consulting LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Brown Bag Consulting LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Brown Bag Consulting LLC.
 */

package com.brownbag.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User: Juan
 * Date: 2/13/11
 * Time: 3:15 PM
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PatternIfThenValidator.class)
@Documented
public @interface PatternIfThen {
    String message() default "Has invalid pattern dependencies";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String ifProperty();

    String ifRegex();

    String thenProperty();

    String thenRegex();
}
