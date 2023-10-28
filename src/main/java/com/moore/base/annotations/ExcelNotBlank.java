package com.moore.base.annotations;

import java.lang.annotation.*;

/**
 * excel not null
 *
 * @author moore
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelNotBlank {

}
