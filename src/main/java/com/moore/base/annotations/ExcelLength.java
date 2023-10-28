package com.moore.base.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * excel size
 *
 * @author moore
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RUNTIME)
public @interface ExcelLength {

    int min() default 0;

    int max() default 2147483647;

}
