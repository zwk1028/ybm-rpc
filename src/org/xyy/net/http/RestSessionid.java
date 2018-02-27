package org.xyy.net.http;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 只能注解于Service类的方法的参数或参数内的String字段
 *
 */
@Inherited
@Documented
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface RestSessionid {

    boolean create() default false;
}
